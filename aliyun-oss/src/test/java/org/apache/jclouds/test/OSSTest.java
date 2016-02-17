package org.apache.jclouds.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.io.ContentMetadataBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class OSSTest {

   private static BlobStore blobStore;

   private static final String provider = "aliyun-oss";
   private static final String key = "accessKey";
   private static final String secret = "accessKeySecret";

   private static final String testRegion = "oss-cn-beijing";
   private static final String testBucket = UUID.randomUUID().toString();

   @BeforeSuite
   public void beforeSuite() {
      BasicConfigurator.configure();
      BlobStoreContext context = ContextBuilder
            .newBuilder(provider)
            .credentials(key, secret)
            .buildView(BlobStoreContext.class);
      blobStore = context.getBlobStore();
   }

   @AfterSuite
   public void afterSuite() {
   }

   @BeforeTest
   public void beforeTest() {
      Iterator<? extends StorageMetadata> it = blobStore.list().iterator();
      while (it.hasNext()) {
         StorageMetadata sm = it.next();
         blobStore.deleteContainer(sm.getName());
      }
   }

   @AfterTest
   public void afterTest() {
   }

   @BeforeClass
   public void beforeClass() {
      LocationBuilder location = new LocationBuilder()
            .scope(LocationScope.REGION)
            .id(testRegion)
            .description(testRegion);
      blobStore.createContainerInLocation(
            location.build(), testBucket);
   }

   @AfterClass
   public void afterClass() {
      blobStore.deleteContainer(testBucket);
   }

   @Test
   public void containerExists() {
      blobStore.containerExists(testBucket);
   }

   @Test
   public void createDirectory() {
      blobStore.createDirectory(testBucket, "test-directory");
      blobStore.setContainerAccess(testBucket, ContainerAccess.PUBLIC_READ);
      blobStore.getContainerAccess(testBucket);
      blobStore.directoryExists(testBucket, "test-directory");
   }

   @Test
   public void putBlob() {
      byte[] bs = new byte[100 * 1024];
      for (int i = 0; i < 100 * 1024; i++) {
         bs[i] = 0;
      }
      Blob blob1 = blobStore.blobBuilder("putblob-1").payload(bs).build();
      blobStore.putBlob(testBucket, blob1);
      Blob blob2 = blobStore.blobBuilder("putblob-2").payload(bs).build();
      blobStore.putBlob(testBucket, blob2, PutOptions.NONE);
   }

   @Test
   public void createContainerInLocation() {
      LocationBuilder location = new LocationBuilder()
            .scope(LocationScope.REGION)
            .id(testRegion)
            .description(testRegion);
      blobStore.createContainerInLocation(location.build(), "bucket-to-delete", CreateContainerOptions.NONE);
      blobStore.createDirectory("bucket-to-delete", "test-directory");
      blobStore.clearContainer("bucket-to-delete");
      blobStore.deleteContainerIfEmpty("bucket-to-delete");
   }

   @Test
   public void list() {
      blobStore.list();
      blobStore.list(testBucket);
      blobStore.list(testBucket, ListContainerOptions.NONE);
   }

   @Test
   public void listConfig() {
      blobStore.listAssignableLocations();
      blobStore.getMaximumMultipartPartSize();
      blobStore.getMaximumNumberOfParts();
      blobStore.getMinimumMultipartPartSize();
   }

   @Test
   public void multipartUpload() {
      BlobMetadata blobMetadata = new BlobMetadataImpl(
            "mp-blob", "mp-blob",
            null, null, null, null, null,
            ImmutableMap.<String, String>builder().build(), null, null,
            ContentMetadataBuilder.create().build());
      MultipartUpload mpu = blobStore.initiateMultipartUpload(testBucket, blobMetadata, PutOptions.NONE);
      byte[] bs = new byte[100 * 1024];
      for (int i = 0; i < 100 * 1024; i++) {
         bs[i] = 0;
      }
      Blob blob1 = blobStore.blobBuilder(UUID.randomUUID().toString()).payload(bs).build();
      Blob blob2 = blobStore.blobBuilder(UUID.randomUUID().toString()).payload(bs).build();
      MultipartPart mp1 = blobStore.uploadMultipartPart(mpu, 1, blob1.getPayload());
      MultipartPart mp2 = blobStore.uploadMultipartPart(mpu, 2, blob2.getPayload());
      List<MultipartPart> parts = new ArrayList<MultipartPart>();
      parts.add(mp1);
      parts.add(mp2);
      blobStore.listMultipartUpload(mpu);
      blobStore.completeMultipartUpload(mpu, parts);
      blobStore.setBlobAccess(testBucket, "mp-blob", BlobAccess.PRIVATE);
      blobStore.getBlobAccess(testBucket, "mp-blob");
      blobStore.getBlob(testBucket, "mp-blob");
      blobStore.getBlob(testBucket, "mp-blob", GetOptions.NONE);
      blobStore.blobExists(testBucket, "mp-blob");
      blobStore.removeBlob(testBucket, "mp-blob");
   }

   @Test
   public void abortMultipartUpload() {
      BlobMetadata blobMetadata = new BlobMetadataImpl(
            "abortblob", "abortblob",
            null, null, null, null, null,
            ImmutableMap.<String, String>builder().build(), null, null,
            ContentMetadataBuilder.create().build());
      MultipartUpload mpu = blobStore.initiateMultipartUpload(testBucket, blobMetadata, PutOptions.NONE);
      byte[] bs = new byte[100 * 1024];
      for (int i = 0; i < 100 * 1024; i++) {
         bs[i] = 0;
      }
      Blob blob1 = blobStore.blobBuilder(UUID.randomUUID().toString()).payload(bs).build();
      Blob blob2 = blobStore.blobBuilder(UUID.randomUUID().toString()).payload(bs).build();
      MultipartPart mp1 = blobStore.uploadMultipartPart(mpu, 1, blob1.getPayload());
      MultipartPart mp2 = blobStore.uploadMultipartPart(mpu, 2, blob2.getPayload());
      List<MultipartPart> parts = new ArrayList<MultipartPart>();
      parts.add(mp1);
      parts.add(mp2);
      blobStore.abortMultipartUpload(mpu);
   }
}
