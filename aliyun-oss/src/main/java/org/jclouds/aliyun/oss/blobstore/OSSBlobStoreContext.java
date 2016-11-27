package org.jclouds.aliyun.oss.blobstore;

import org.jclouds.aliyun.oss.blobstore.internal.OSSBlobStoreContextImpl;
import org.jclouds.blobstore.BlobStoreContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(OSSBlobStoreContextImpl.class)
public interface OSSBlobStoreContext extends BlobStoreContext {

   @Override
   OSSBlobStore getBlobStore();
}
