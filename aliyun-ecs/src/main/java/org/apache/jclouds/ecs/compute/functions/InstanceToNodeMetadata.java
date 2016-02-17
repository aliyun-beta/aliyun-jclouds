package org.apache.jclouds.ecs.compute.functions;

import java.util.Map;

import org.apache.jclouds.ecs.ECSApi;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse.Instance;
import com.google.common.base.Function;

public class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

   protected final ECSApi api;
   protected final Map<Instance.Status, NodeMetadata.Status> status;

   public InstanceToNodeMetadata(
         ECSApi api,
         Map<Instance.Status, NodeMetadata.Status> status) {
      this.api = api;
      this.status = status;
   }

   @Override
   public NodeMetadata apply(Instance input) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(api.encodeToId(input.getRegionId(), input.getInstanceId()));
      builder.providerId(input.getInstanceId());
      builder.name(input.getInstanceName());
      builder.group(input.getRegionId());
      builder.status(status.get(input.getStatus()));
      builder.imageId(input.getImageId());
      Hardware hardware = new HardwareBuilder()
            .id(input.getInstanceType())
            .build();
      builder.hardware(hardware);
      Location location = new LocationBuilder()
            .scope(LocationScope.REGION)
            .id(input.getRegionId())
            .description(input.getRegionId())
            .build();
      builder.location(location);
      builder.publicAddresses(input.getPublicIpAddress());
      builder.privateAddresses(input.getInnerIpAddress());
      return builder.build();
   }
}
