package org.apache.jclouds.oss.blobstore;

import org.apache.jclouds.oss.blobstore.internal.OSSBlobStoreContextImpl;
import org.jclouds.blobstore.BlobStoreContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(OSSBlobStoreContextImpl.class)
public interface OSSBlobStoreContext extends BlobStoreContext {

   @Override
   OSSBlobStore getBlobStore();
}
