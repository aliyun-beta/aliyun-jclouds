package org.apache.jclouds.oss.blobstore.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.jclouds.oss.blobstore.OSSBlobStore;
import org.apache.jclouds.oss.blobstore.OSSBlobStoreContext;
import org.jclouds.Context;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.Utils;

import com.google.common.reflect.TypeToken;

@Singleton
public class OSSBlobStoreContextImpl extends BlobStoreContextImpl implements OSSBlobStoreContext {

   @Inject
   public OSSBlobStoreContextImpl(
         Context backend,
         @Provider TypeToken<? extends Context> backendType,
         Utils utils,
         ConsistencyModel consistencyModel,
         BlobStore blobStore,
         BlobRequestSigner blobRequestSigner) {
      super(backend, backendType, utils, consistencyModel, blobStore, blobRequestSigner);
   }

   @Override
   public OSSBlobStore getBlobStore() {
      return OSSBlobStore.class.cast(super.getBlobStore());
   }
}
