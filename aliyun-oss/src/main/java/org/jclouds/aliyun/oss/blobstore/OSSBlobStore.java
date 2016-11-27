package org.jclouds.aliyun.oss.blobstore;

import static com.google.common.collect.Iterables.transform;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aliyun.oss.OSSApi;
import org.jclouds.aliyun.oss.blobstore.functions.RegionToLocation;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.logging.Logger;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.Grant;
import com.aliyun.oss.model.GroupGrantee;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectPermission;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Singleton
public class OSSBlobStore extends BaseBlobStore {

   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   private Logger logger = Logger.NULL;

   protected final OSSApi api;
   protected final Map<ContainerAccess, CannedAccessControlList> bucketAccesses;
   protected final Map<BlobAccess, CannedAccessControlList> blobAccesses;

   @Inject
   protected OSSBlobStore(
         OSSApi api,
         BlobStoreContext context,
         BlobUtils blobUtils,
         Supplier<Location> defaultLocation,
         LocationsSupplier locations,
         Supplier<Map<ContainerAccess, CannedAccessControlList>> bucketAccesses,
         Supplier<Map<BlobAccess, CannedAccessControlList>> blobAccesses) {
      super(context, blobUtils, defaultLocation, locations);
      this.api = api;
      this.bucketAccesses = bucketAccesses.get();
      this.blobAccesses = blobAccesses.get();
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return ImmutableSet.<Location> builder()
            .addAll(transform(api.getAvailableRegions(), new RegionToLocation()))
            .build();
   }

   @Override
   public boolean directoryExists(String container, String directory) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      return oss.doesObjectExist(container, directory + BlobStoreConstants.DIRECTORY_BLOB_SUFFIX);
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      PageSetImpl<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(
            transform(oss.listBuckets(), new Function<Bucket, StorageMetadata>() {
                  @Override
                  public StorageMetadata apply(Bucket input) {
                     String bucketLocation = input.getLocation();
                     Location location = new LocationBuilder()
                           .id(bucketLocation)
                           .scope(LocationScope.REGION)
                           .description(bucketLocation)
                           .build();
                     StorageMetadata storageMetadata = new StorageMetadataImpl(
                           StorageType.CONTAINER,
                           input.getName(),
                           input.getName(),
                           location,
                           null,
                           null,
                           input.getCreationDate(),
                           input.getCreationDate(),
                           ImmutableMap.<String, String>builder().build(),
                           0L);
                     return storageMetadata;
                  }
               }), null);
      return pageSet;
   }

   @Override
   public boolean containerExists(String container) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      return oss.doesBucketExist(container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      OSS oss = api.getOSSClient(location.getId());
      boolean result = true;
      try {
         oss.createBucket(container);
      } catch (Exception e) {
         result = false;
      }
      return result;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      OSS oss = api.getOSSClient(location.getId());
      boolean result = true;
      try {
         CreateBucketRequest req = new CreateBucketRequest(container);
         CannedAccessControlList acl = CannedAccessControlList.Private;
         if (options.isPublicRead()) {
            acl = CannedAccessControlList.PublicRead;
         }
         req.setCannedACL(acl);
         oss.createBucket(req);
      } catch (Exception e) {
         result = false;
      }
      return result;
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      AccessControlList acl = oss.getBucketAcl(container);
      ContainerAccess access = ContainerAccess.PRIVATE;
      for (Grant grant : acl.getGrants()) {
         if (GroupGrantee.AllUsers.equals(grant.getGrantee())) {
            access = ContainerAccess.PUBLIC_READ;
         }
      }
      return access;
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      oss.setBucketAcl(container, bucketAccesses.get(access));
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      final OSS foss = api.getOSSClient(region);
      final Location location = new LocationBuilder()
            .id(region)
            .scope(LocationScope.REGION)
            .description(region)
            .build();
      ListObjectsRequest req = new ListObjectsRequest(container);
      if (options.getDir() != null) {
         req.setPrefix(options.getDir());
      }
      if (options.getMaxResults() != null) {
         req.setMaxKeys(options.getMaxResults());
      }
      if (options.getMarker() != null) {
         req.setMarker(options.getMarker());
      }
      ObjectListing listing = foss.listObjects(req);
      PageSetImpl<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(
            transform(listing.getObjectSummaries(), new Function<OSSObjectSummary, StorageMetadata>() {
                  @Override
                  public StorageMetadata apply(OSSObjectSummary input) {
                     URI uri = null;
                     StorageType storagetType = StorageType.BLOB;
                     if (input.getKey().endsWith("/")) {
                        storagetType = StorageType.FOLDER;
                     } else {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR, time.get(Calendar.HOUR) + 1);
                        Date expires = time.getTime();
                        URL url = foss.generatePresignedUrl(input.getBucketName(), input.getKey(), expires);
                        try {
                           uri = url.toURI();
                        } catch (URISyntaxException e) {
                           logger.warn(e.getMessage());
                        }
                     }
                     StorageMetadata storageMetadata = new StorageMetadataImpl(
                           storagetType,
                           input.getKey(),
                           input.getKey(),
                           location,
                           uri,
                           input.getETag(),
                           input.getLastModified(),
                           input.getLastModified(),
                           ImmutableMap.<String, String>builder().build(),
                           input.getSize());
                     return storageMetadata;
                  }
               }), listing.getNextMarker());
      return pageSet;
   }

   @Override
   public void clearContainer(final String container) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      final OSS foss = api.getOSSClient(region);
      Function<ListObjectsRequest, ListObjectsRequest> func = new Function<ListObjectsRequest, ListObjectsRequest>() {
         @Override
         public ListObjectsRequest apply(ListObjectsRequest input) {
            ObjectListing listing = foss.listObjects(input);
            DeleteObjectsRequest dreq = new DeleteObjectsRequest(container);
            List<String> keys = ImmutableList.<String> builder()
                  .addAll(transform(listing.getObjectSummaries(), new Function<OSSObjectSummary, String>() {
                     @Override
                     public String apply(OSSObjectSummary input) {
                        return input.getKey();
                     }
                  }))
                  .build();
            ListObjectsRequest next = null;
            if (listing.isTruncated()) {
               input.setMarker(listing.getNextMarker());
               next = input;
            }
            if (keys.size() > 0) {
               dreq.setKeys(keys);
               foss.deleteObjects(dreq);
            }
            return next;
         }
      };
      ListObjectsRequest lreq = new ListObjectsRequest(container);
      while (lreq != null) {
         lreq = func.apply(lreq);
      }
   }

   @Override
   public void clearContainer(final String container, ListContainerOptions options) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      final OSS foss = api.getOSSClient(region);
      Function<ListObjectsRequest, ListObjectsRequest> func = new Function<ListObjectsRequest, ListObjectsRequest>() {
         @Override
         public ListObjectsRequest apply(ListObjectsRequest input) {
            ObjectListing listing = foss.listObjects(input);
            DeleteObjectsRequest dreq = new DeleteObjectsRequest(container);
            List<String> keys = ImmutableList.<String> builder()
                  .addAll(transform(listing.getObjectSummaries(), new Function<OSSObjectSummary, String>() {
                     @Override
                     public String apply(OSSObjectSummary input) {
                        return input.getKey();
                     }
                  }))
                  .build();
            ListObjectsRequest next = null;
            if (listing.isTruncated()) {
               input.setMarker(listing.getNextMarker());
               next = input;
            }
            if (keys.size() > 0) {
               dreq.setKeys(keys);
               foss.deleteObjects(dreq);
            }
            return next;
         }
      };
      ListObjectsRequest lreq = new ListObjectsRequest(container);
      if (options.getDir() != null) {
         lreq.setPrefix(options.getDir());
      }
      if (options.getMaxResults() != null) {
         lreq.setMaxKeys(options.getMaxResults());
      }
      if (options.getMarker() != null) {
         lreq.setMarker(options.getMarker());
      }
      while (lreq != null) {
         lreq = func.apply(lreq);
      }
   }

   @Override
   public boolean blobExists(String container, String name) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      return oss.doesObjectExist(container, name);
   }

   @Override
   public String putBlob(String container, Blob blob) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      PutObjectResult result = null;
      try {
         result = oss.putObject(container, blob.getMetadata().getProviderId(), blob.getPayload().openStream());
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return result.getETag();
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      PutObjectResult result = null;
      try {
         result = oss.putObject(container, blob.getMetadata().getProviderId(), blob.getPayload().openStream());
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      return result.getETag();
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      OSSObject object = oss.getObject(container, name);
      Calendar time = Calendar.getInstance();
      time.set(Calendar.HOUR, time.get(Calendar.HOUR) + 1);
      Date expires = time.getTime();
      URL url = oss.generatePresignedUrl(container, name, expires);
      URI uri = null;
      try {
         uri = url.toURI();
      } catch (URISyntaxException e) {
         logger.warn(e, e.getMessage());
      }
      Location location = new LocationBuilder()
            .id(region)
            .scope(LocationScope.REGION)
            .description(region)
            .build();
      ContentMetadata cm = ContentMetadataBuilder.create()
            .expires(expires)
            .contentDisposition(object.getObjectMetadata().getContentDisposition())
            .contentEncoding(object.getObjectMetadata().getContentEncoding())
            .contentLength(object.getObjectMetadata().getContentLength())
            .contentType(object.getObjectMetadata().getContentType())
            .build();
      return new BlobMetadataImpl(
            object.getKey(),
            object.getKey(),
            location,
            uri,
            object.getObjectMetadata().getETag(),
            object.getObjectMetadata().getLastModified(),
            object.getObjectMetadata().getLastModified(),
            object.getObjectMetadata().getUserMetadata(),
            uri,
            object.getBucketName(),
            cm);
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      GetObjectRequest req = new GetObjectRequest(container, name);
      OSSObject object = oss.getObject(req);
      String filename = object.getKey();
      return blobBuilder(container)
            .name(filename)
            .payload(object.getObjectContent())
            .build();
   }

   @Override
   public void removeBlob(String container, String name) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      oss.deleteObject(container, name);
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      ObjectAcl acl = oss.getObjectAcl(container, name);
      BlobAccess access = BlobAccess.PRIVATE;
      if (acl.getPermission().equals(ObjectPermission.Private)) {
         access = BlobAccess.PRIVATE;
      } else if (acl.getPermission().equals(ObjectPermission.PublicRead)) {
         access = BlobAccess.PUBLIC_READ;
      } else if (acl.getPermission().equals(ObjectPermission.PublicReadWrite)) {
         access = BlobAccess.PUBLIC_READ;
      } else {
         AccessControlList bacl = oss.getBucketAcl(container);
         ContainerAccess baccess = ContainerAccess.PRIVATE;
         for (Grant grant : bacl.getGrants()) {
            if (GroupGrantee.AllUsers.equals(grant.getGrantee())) {
               baccess = ContainerAccess.PUBLIC_READ;
            }
         }
         if (baccess.equals(ContainerAccess.PUBLIC_READ)) {
            access = BlobAccess.PUBLIC_READ;
         }
      }
      return access;
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      oss.setObjectAcl(container, name, blobAccesses.get(access));
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      OSS oss = api.getOSSClient(OSSApi.DEFAULT_REGION);
      String region = oss.getBucketLocation(container);
      oss = api.getOSSClient(region);
      boolean result = true;
      if (oss.listObjects(container).getObjectSummaries().size() > 0) {
         result = false;
      } else {
         try {
            oss.deleteBucket(container);
         } catch (Exception e) {
            result = false;
         }
      }
      return result;
   }
}
