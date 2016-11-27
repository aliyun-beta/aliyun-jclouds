package org.jclouds.aliyun.oss;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aliyun.oss.blobstore.OSSBlobStoreContext;
import org.jclouds.aliyun.oss.blobstore.config.OSSBlobStoreContextModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class OSSApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public OSSApiMetadata() {
      this(new Builder());
   }

   protected OSSApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("aliyun-oss-api")
         .name("aliyun oss api")
         .identityName("identity")
         .documentation(URI.create("https://help.aliyun.com/document_detail/oss/api-reference/abstract.html"))
         .defaultEndpoint("http://oss.aliyuncs.com")
         .defaultProperties(OSSApiMetadata.defaultProperties())
         .view(OSSBlobStoreContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(OSSBlobStoreContextModule.class)
               .add(Log4JLoggingModule.class)
               .build());
      }

      @Override
      public ApiMetadata build() {
         return new OSSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
