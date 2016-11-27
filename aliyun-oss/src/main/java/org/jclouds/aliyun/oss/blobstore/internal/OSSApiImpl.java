package org.jclouds.aliyun.oss.blobstore.internal;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aliyun.oss.OSSApi;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.RegionIdsSupplier;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.google.common.base.Supplier;

public class OSSApiImpl implements OSSApi {

   protected static final String INTERNAL_ENDPOINT_SUFFIX = "-internal.aliyuncs.com";
   protected static final String INTERNET_ENDPOINT_SUFFIX = ".aliyuncs.com";

   private final String identity;
   private final String credential;
   private final Set<String> regions;

   @Inject
   public OSSApiImpl(
         @Provider final Supplier<Credentials> creds,
         RegionIdsSupplier regions) {
      this.identity = creds.get().identity;
      this.credential = creds.get().credential;
      this.regions = regions.get();
   }

   @Override
   public OSS getOSSClient(String region) {
      String endpoint = region + INTERNET_ENDPOINT_SUFFIX;
      return new OSSClient(endpoint, identity, credential);
   }

   @Override
   public Set<String> getAvailableRegions() {
      return regions;
   }

   @Override
   public void close() throws IOException {}
}
