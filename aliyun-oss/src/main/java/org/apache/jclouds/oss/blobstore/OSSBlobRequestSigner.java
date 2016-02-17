package org.apache.jclouds.oss.blobstore;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.http.HttpRequest;

@Singleton
public class OSSBlobRequestSigner implements BlobRequestSigner {

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, GetOptions options) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      HttpRequest req = HttpRequest.builder()
            .build();
      return req;
   }
}
