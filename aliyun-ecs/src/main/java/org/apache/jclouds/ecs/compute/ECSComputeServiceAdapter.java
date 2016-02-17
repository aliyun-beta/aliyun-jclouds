package org.apache.jclouds.ecs.compute;

import static com.google.common.collect.Iterables.transform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.jclouds.ecs.ECSApi;
import org.apache.jclouds.ecs.compute.functions.ImageToImage;
import org.apache.jclouds.ecs.compute.functions.InstanceToNodeMetadata;
import org.apache.jclouds.ecs.compute.functions.RegionToLocation;
import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.CreateInstanceRequest;
import com.aliyuncs.ecs.model.v20140526.CreateInstanceResponse;
import com.aliyuncs.ecs.model.v20140526.CreateSecurityGroupRequest;
import com.aliyuncs.ecs.model.v20140526.CreateSecurityGroupResponse;
import com.aliyuncs.ecs.model.v20140526.DeleteInstanceRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeImagesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeImagesResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse.Instance;
import com.aliyuncs.ecs.model.v20140526.DescribeSecurityGroupsRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeSecurityGroupsResponse;
import com.aliyuncs.ecs.model.v20140526.JoinSecurityGroupRequest;
import com.aliyuncs.ecs.model.v20140526.RebootInstanceRequest;
import com.aliyuncs.ecs.model.v20140526.StartInstanceRequest;
import com.aliyuncs.ecs.model.v20140526.StopInstanceRequest;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;;

public class ECSComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final ECSApi api;
   protected final Map<Instance.Status, NodeMetadata.Status> nodeStatus;

   @Inject
   public ECSComputeServiceAdapter(
         ECSApi api,
         Map<Instance.Status, NodeMetadata.Status> nodeStatus) {
      this.api = api;
      this.nodeStatus = nodeStatus;
   }

   @Override
   public Image getImage(String id) {
      Image image = null;
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      DescribeImagesRequest req = new DescribeImagesRequest();
      req.setImageId(api.decodeToId(id));
      try {
         DescribeImagesResponse resp = client.getAcsResponse(req);
         if (resp.getTotalCount() > 0) {
            Iterator<Image> it = transform(resp.getImages(), new ImageToImage(api, resp.getRegionId())).iterator();
            if (it.hasNext()) {
               image = it.next();
            }
         }
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return image;
   }

   @Override
   public void resumeNode(String id) {
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      StartInstanceRequest req = new StartInstanceRequest();
      req.setInstanceId(api.decodeToId(id));
      try {
         client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   @Override
   public void suspendNode(String id) {
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      StopInstanceRequest req = new StopInstanceRequest();
      req.setInstanceId(api.decodeToId(id));
      try {
         client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   @Override
   public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
      List<Instance> instances = new ArrayList<Instance>();
      for (String id : ids) {
         IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
         DescribeInstancesRequest req = new DescribeInstancesRequest();
         Gson gson = new GsonBuilder().create();
         String iids = gson.toJson(new String[] { api.decodeToId(id) });
         req.setInstanceIds(iids);
         try {
            DescribeInstancesResponse resp = client.getAcsResponse(req);
            instances.addAll(resp.getInstances());
         } catch (Exception e) {
            logger.warn(e.getMessage());
         }
      }
      Builder<NodeMetadata> builder = ImmutableSet.builder();
      builder.addAll(transform(instances, new InstanceToNodeMetadata(api, nodeStatus)));
      return builder.build();
   }

   @Override
   public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      NodeWithInitialCredentials nodeWithCred = null;
      IAcsClient client = api.getAcsClient(group);
      try {
         CreateInstanceRequest cireq = new CreateInstanceRequest();
         cireq.setImageId(api.decodeToId(template.getImage().getId()));
         cireq.setInstanceType(template.getHardware().getId());
         Set<String> securityGroups = template.getOptions().getGroups();
         if (securityGroups.size() == 0) {
            DescribeSecurityGroupsRequest sgreq = new DescribeSecurityGroupsRequest();
            DescribeSecurityGroupsResponse sgresp = client.getAcsResponse(sgreq);
            if (sgresp.getSecurityGroups().size() > 0) {
               securityGroups = ImmutableSet.<String> builder()
                     .add(sgresp.getSecurityGroups().get(0).getSecurityGroupId())
                     .build();
            } else {
               CreateSecurityGroupRequest csgreq = new CreateSecurityGroupRequest();
               CreateSecurityGroupResponse csgresp = client.getAcsResponse(csgreq);
               securityGroups = ImmutableSet.<String> builder()
                     .add(csgresp.getSecurityGroupId())
                     .build();
            }
         }
         Iterator<String> sgit = securityGroups.iterator();
         cireq.setSecurityGroupId(sgit.next());
         CreateInstanceResponse resp = client.getAcsResponse(cireq);
         String instanceId = resp.getInstanceId();
         if (sgit.hasNext()) {
            String securityGroup = sgit.next();
            JoinSecurityGroupRequest jsgreq = new JoinSecurityGroupRequest();
            jsgreq.setInstanceId(instanceId);
            jsgreq.setSecurityGroupId(securityGroup);
            client.getAcsResponse(jsgreq);
         }
         NodeMetadata nodeMetadata = this.getNode(api.encodeToId(group, instanceId));
         nodeWithCred = new NodeWithInitialCredentials(nodeMetadata);
         TemplateOptions options = template.getOptions();
         if (options.shouldBlockUntilRunning()) {
            this.resumeNode(nodeMetadata.getId());
         }
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return nodeWithCred;
   }

   @Override
   public Iterable<NodeMetadata> listNodes() {
      Builder<NodeMetadata> builder = ImmutableSet.builder();
      Set<String> regions = api.getAvailableRegions();
      for (String region : regions) {
         try {
            IAcsClient client = api.getAcsClient(region);
            DescribeInstancesRequest req = new DescribeInstancesRequest();
            DescribeInstancesResponse resp = client.getAcsResponse(req);
            builder.addAll(transform(resp.getInstances(), new InstanceToNodeMetadata(api, nodeStatus)));
         } catch (Exception e) {
            logger.warn(e.getMessage());
         }
      }
      return builder.build();
   }

   @Override
   public Iterable<Image> listImages() {
      Builder<Image> builder = ImmutableSet.builder();
      Set<String> regions = api.getAvailableRegions();
      for (String region : regions) {
         try {
            IAcsClient client = api.getAcsClient(region);
            DescribeImagesRequest req = new DescribeImagesRequest();
            DescribeImagesResponse resp = client.getAcsResponse(req);
            builder.addAll(transform(resp.getImages(), new ImageToImage(api, region)));
         } catch (Exception e) {
            logger.warn(e.getMessage());
         }
      }
      return builder.build();
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Function<Integer, Iterable<Processor>> func = new Function<Integer, Iterable<Processor>>() {
         @Override
         public Iterable<Processor> apply(Integer input) {
            return ImmutableSet.<Processor> builder()
                  .add(new Processor(input, 0))
                  .build();
         }
      };
      return ImmutableSet.<Hardware> builder()
            .add(new HardwareBuilder()
                  .id("ecs.t1.small")
                  .processors(func.apply(1))
                  .ram(1024)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s1.small")
                  .processors(func.apply(1))
                  .ram(2048)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s1.medium")
                  .processors(func.apply(1))
                  .ram(4096)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s2.small")
                  .processors(func.apply(2))
                  .ram(2048)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s2.large")
                  .processors(func.apply(2))
                  .ram(4096)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s2.xlarge")
                  .processors(func.apply(2))
                  .ram(8192)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s3.medium")
                  .processors(func.apply(4))
                  .ram(4096)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.s3.large")
                  .processors(func.apply(4))
                  .ram(8192)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.m1.medium")
                  .processors(func.apply(4))
                  .ram(16384)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n1.tiny")
                  .processors(func.apply(1))
                  .ram(1024)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n1.small")
                  .processors(func.apply(1))
                  .ram(2048)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n1.medium")
                  .processors(func.apply(2))
                  .ram(4096)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n1.large")
                  .processors(func.apply(4))
                  .ram(8192)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n2.small")
                  .processors(func.apply(1))
                  .ram(4096)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n2.medium")
                  .processors(func.apply(4))
                  .ram(8192)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.n2.large")
                  .processors(func.apply(4))
                  .ram(16384)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.e3.small")
                  .processors(func.apply(1))
                  .ram(8192)
                  .build())
            .add(new HardwareBuilder()
                  .id("ecs.e3.medium")
                  .processors(func.apply(2))
                  .ram(16384)
                  .build())
            .build();
   }

   @Override
   public Iterable<Location> listLocations() {
      Builder<Location> builder = ImmutableSet.builder();
      builder.addAll(transform(api.getAvailableRegions(), new RegionToLocation()));
      return builder.build();
   }

   @Override
   public NodeMetadata getNode(String id) {
      NodeMetadata nodeMetadata = null;
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      DescribeInstancesRequest req = new DescribeInstancesRequest();
      Gson gson = new GsonBuilder().create();
      String ids = gson.toJson(new String[] { api.decodeToId(id) });
      req.setInstanceIds(ids);
      try {
         DescribeInstancesResponse resp = client.getAcsResponse(req);
         if (resp.getTotalCount() > 0) {
            Iterator<NodeMetadata> it = transform(resp.getInstances(), new InstanceToNodeMetadata(api, nodeStatus)).iterator();
            if (it.hasNext()) {
               nodeMetadata = it.next();
            }
         }
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return nodeMetadata;
   }

   @Override
   public void destroyNode(String id) {
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      DeleteInstanceRequest req = new DeleteInstanceRequest();
      req.setInstanceId(api.decodeToId(id));
      try {
         client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   @Override
   public void rebootNode(String id) {
      IAcsClient client = api.getAcsClient(api.decodeToRegion(id));
      RebootInstanceRequest req = new RebootInstanceRequest();
      req.setInstanceId(api.decodeToId(id));
      try {
         client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }
}
