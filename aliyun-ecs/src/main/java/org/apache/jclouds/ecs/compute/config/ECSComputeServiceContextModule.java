package org.apache.jclouds.ecs.compute.config;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.jclouds.ecs.ECSApi;
import org.apache.jclouds.ecs.compute.ECSComputeService;
import org.apache.jclouds.ecs.compute.ECSComputeServiceAdapter;
import org.apache.jclouds.ecs.compute.internal.ECSApiImpl;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse.Instance;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

public class ECSComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

   public ECSComputeServiceContextModule() {
      super(ECSComputeServiceAdapter.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(ECSApi.class).to(ECSApiImpl.class);
      bind(ComputeService.class).to(ECSComputeService.class);
      install(new LocationsFromComputeServiceAdapterModule<NodeMetadata, Hardware, Image, Location>() {});
   }

   @Provides
   @Named("ECS")
   protected final TemplateOptions guiceProvideTemplateOptions(TemplateOptions options) {
      return options.blockUntilRunning(false);
   }

   @Provides
   @Named("ECS")
   protected final TemplateBuilder guiceProvideTemplateBuilder(TemplateBuilder template) {
      return template.imageId("cn-shanghai:aliyun1501_64_20G_aliaegis_20150325.vhd")
            .hardwareId("ecs.t1.small")
            .locationId("cn-hangzhou");
   }

   @Provides
   @Singleton
   protected final Map<Instance.Status, NodeMetadata.Status> getInstanceStatus() {
      return ImmutableMap.<Instance.Status, NodeMetadata.Status> builder()
            .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING)
            .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
            .put(Instance.Status.DELETED, NodeMetadata.Status.TERMINATED)
            .put(Instance.Status.STARTING, NodeMetadata.Status.PENDING)
            .put(Instance.Status.RESETTING, NodeMetadata.Status.PENDING)
            .put(Instance.Status.TRANSFERRING, NodeMetadata.Status.PENDING)
            .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
            .build();
   }
}
