package org.apache.jclouds.slb.loadbalancer;

import static com.google.common.collect.Iterables.transform;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.jclouds.slb.SLBApi;
import org.apache.jclouds.slb.loadbalancer.functions.LoadBalancerToLoadBalancerMetadata;
import org.apache.jclouds.slb.loadbalancer.functions.RegionToLocation;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.logging.Logger;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.slb.model.v20140515.CreateLoadBalancerRequest;
import com.aliyuncs.slb.model.v20140515.CreateLoadBalancerResponse;
import com.aliyuncs.slb.model.v20140515.DeleteLoadBalancerRequest;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancerAttributeRequest;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancerAttributeResponse;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancersRequest;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancersResponse.LoadBalancer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class SLBLoadBalancerServiceAdapter implements LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> {

   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final SLBApi api;
   protected final Map<String, LoadBalancerType> loadBalancerTypes;

   @Inject
   public SLBLoadBalancerServiceAdapter(
         SLBApi api,
         Map<String, LoadBalancerType> loadBalancerTypes) {
      this.api = api;
      this.loadBalancerTypes = loadBalancerTypes;
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(
         Location location,
         String loadBalancerName,
         String protocol,
         int loadBalancerPort,
         int instancePort,
         Iterable<? extends NodeMetadata> nodes) {
      IAcsClient client = api.getAcsClient(location.getId());
      CreateLoadBalancerRequest req = new CreateLoadBalancerRequest();
      req.setAddressType("intranet");
      req.setLoadBalancerName(loadBalancerName);
      CreateLoadBalancerResponse resp = null;
      LoadBalancerMetadata lbm = null;
      try {
         resp = client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
         return null;
      }
      lbm = new LoadBalancerMetadataImpl(
            loadBalancerTypes.get(req.getAddressType()),
            resp.getLoadBalancerId(),
            resp.getLoadBalancerName(),
            resp.getLoadBalancerId(),
            location,
            null,
            ImmutableMap.<String, String>builder().build(),
            ImmutableSet.<String> builder().add(resp.getAddress()).build());
      return lbm;
   }

   @Override
   public Iterable<Location> listAssignableLocations() {
      Builder<Location> builder = ImmutableSet.builder();
      builder.addAll(transform(api.getAvailableRegions(), new RegionToLocation()));
      return builder.build();
   }

   @Override
   public LoadBalancerMetadata getLoadBalancer(String id) {
      IAcsClient client = api.getAcsClient(SLBApi.DEFAULT_REGION);
      DescribeLoadBalancerAttributeRequest req = new DescribeLoadBalancerAttributeRequest();
      req.setLoadBalancerId(id);
      LoadBalancerMetadata lbm = null;
      try {
         DescribeLoadBalancerAttributeResponse resp = client.getAcsResponse(req);
         LocationBuilder location = new LocationBuilder()
               .scope(LocationScope.REGION)
               .id(resp.getRegionId())
               .description(resp.getRegionId());
         lbm = new LoadBalancerMetadataImpl(
               loadBalancerTypes.get(resp.getAddressType()),
               resp.getLoadBalancerId(),
               resp.getLoadBalancerName(),
               resp.getLoadBalancerId(),
               location.build(),
               null,
               ImmutableMap.<String, String>builder().build(),
               ImmutableSet.<String> builder().add(resp.getAddress()).build());
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return lbm;
   }

   @Override
   public void destroyLoadBalancer(String id) {
      IAcsClient client = api.getAcsClient(SLBApi.DEFAULT_REGION);
      DeleteLoadBalancerRequest req = new DeleteLoadBalancerRequest();
      req.setLoadBalancerId(id);
      try {
         client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   @Override
   public Iterable<LoadBalancerMetadata> listLoadBalancers() {
      Builder<LoadBalancerMetadata> builder = ImmutableSet.builder();
      for (String region : api.getAvailableRegions()) {
         IAcsClient client = api.getAcsClient(region);
         DescribeLoadBalancersRequest req = new DescribeLoadBalancersRequest();
         try {
            List<LoadBalancer> list = client.getAcsResponse(req).getLoadBalancers();
            builder.addAll(transform(list, new LoadBalancerToLoadBalancerMetadata(client, loadBalancerTypes)));
         } catch (Exception e) {
            logger.warn(e.getMessage());
         }
      }
      return builder.build();
   }
}
