package org.apache.jclouds.oss;

import java.util.Properties;

import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public class OSSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public OSSProviderMetadata() {
      this(builder());
   }

   protected OSSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(
            BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX,
            BlobStoreConstants.DIRECTORY_SUFFIX_ROOT);
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("aliyun-oss")
         .name("aliyun oss")
         .apiMetadata(new OSSApiMetadata())
         .defaultProperties(OSSProviderMetadata.defaultProperties());
      }

      @Override
      public ProviderMetadata build() {
         return new OSSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
