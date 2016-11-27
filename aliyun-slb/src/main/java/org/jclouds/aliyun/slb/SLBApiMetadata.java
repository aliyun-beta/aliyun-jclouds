package org.jclouds.aliyun.slb;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aliyun.slb.loadbalancer.SLBLoadBalancerServiceContext;
import org.jclouds.aliyun.slb.loadbalancer.config.SLBLoadBalancerServiceContextModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class SLBApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SLBApiMetadata() {
      this(new Builder());
   }

   protected SLBApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("aliyun-slb-api")
         .name("aliyun slb api")
         .identityName("identity")
         .documentation(URI.create("https://help.aliyun.com/document_detail/slb/api-reference/brief-introduction.html"))
         .defaultEndpoint("http://slb.aliyuncs.com")
         .defaultProperties(SLBApiMetadata.defaultProperties())
         .view(SLBLoadBalancerServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(SLBLoadBalancerServiceContextModule.class)
               .add(Log4JLoggingModule.class)
               .build());
      }

      @Override
      public ApiMetadata build() {
         return new SLBApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
