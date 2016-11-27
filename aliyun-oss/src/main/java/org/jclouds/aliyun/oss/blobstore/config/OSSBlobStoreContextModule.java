package org.jclouds.aliyun.oss.blobstore.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aliyun.oss.OSSApi;
import org.jclouds.aliyun.oss.blobstore.OSSBlobRequestSigner;
import org.jclouds.aliyun.oss.blobstore.OSSBlobStore;
import org.jclouds.aliyun.oss.blobstore.internal.OSSApiImpl;
import org.jclouds.aliyun.oss.blobstore.internal.OSSBlobBuilder;
import org.jclouds.aliyun.oss.blobstore.internal.OSSBlobUtils;
import org.jclouds.aliyun.oss.blobstore.internal.OSSLocationsSupplier;
import org.jclouds.aliyun.oss.blobstore.internal.OSSRegionIdsSupplier;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;

import com.aliyun.oss.model.CannedAccessControlList;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class OSSBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(OSSApi.class).to(OSSApiImpl.class);
      bind(BlobStore.class).to(OSSBlobStore.class);
      bind(BlobRequestSigner.class).to(OSSBlobRequestSigner.class);
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(BlobBuilder.class).to(OSSBlobBuilder.class);
      bind(BlobUtils.class).to(OSSBlobUtils.class);
      bind(LocationsSupplier.class).to(OSSLocationsSupplier.class);
      bind(RegionIdsSupplier.class).to(OSSRegionIdsSupplier.class);
   }

   @Provides
   @Singleton
   protected final Supplier<Map<BlobAccess, CannedAccessControlList>> accessFromBlobToOSS() {
      Map<BlobAccess, CannedAccessControlList> regions = ImmutableMap
            .<BlobAccess, CannedAccessControlList>builder()
            .put(BlobAccess.PRIVATE, CannedAccessControlList.Private)
            .put(BlobAccess.PUBLIC_READ, CannedAccessControlList.PublicRead)
            .build();
      return Suppliers.ofInstance(regions);
   }

   @Provides
   @Singleton
   protected final Supplier<Map<ContainerAccess, CannedAccessControlList>> accessFromContainerToOSS() {
      Map<ContainerAccess, CannedAccessControlList> regions = ImmutableMap
            .<ContainerAccess, CannedAccessControlList>builder()
            .put(ContainerAccess.PRIVATE, CannedAccessControlList.Private)
            .put(ContainerAccess.PUBLIC_READ, CannedAccessControlList.PublicRead)
            .build();
      return Suppliers.ofInstance(regions);
   }
}
