package org.apache.jclouds.oss.blobstore.internal;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;

public class OSSBlobBuilder extends BlobBuilderImpl {

   @Override
   public Blob build() {
      Blob blob = super.build();
      if (blob.getMetadata().getProviderId() == null) {
         blob.getMetadata().setId(blob.getMetadata().getName());;
      }
      return blob;
   }
}
