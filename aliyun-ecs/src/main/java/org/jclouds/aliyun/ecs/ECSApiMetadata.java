package org.jclouds.aliyun.ecs;

import java.net.URI;

import org.jclouds.aliyun.ecs.compute.ECSComputeServiceContext;
import org.jclouds.aliyun.ecs.compute.config.ECSComputeServiceContextModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class ECSApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder();
   }

   public ECSApiMetadata() {
      this(new Builder());
   }

   protected ECSApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("aliyun-ecs-api")
         .name("aliyun ecs api")
         .identityName("identity")
         .documentation(URI.create("https://help.aliyun.com/document_detail/ecs/open-api/summary.html"))
         .defaultEndpoint("http://ecs.aliyuncs.com")
         .view(ECSComputeServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(ECSComputeServiceContextModule.class)
               .add(Log4JLoggingModule.class)
               .build());
      }

      @Override
      public ApiMetadata build() {
         return new ECSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
