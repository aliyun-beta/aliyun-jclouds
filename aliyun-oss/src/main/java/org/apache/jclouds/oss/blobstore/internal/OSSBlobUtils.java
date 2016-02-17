package org.apache.jclouds.oss.blobstore.internal;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.DeleteDirectoryStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.blobstore.util.internal.BlobUtilsImpl;

public class OSSBlobUtils extends BlobUtilsImpl {

   protected final BlobBuilder blobBuilder;

   @Inject
   protected OSSBlobUtils(
         BlobBuilder blobBuilder,
         Provider<BlobBuilder> blobBuilders,
         ClearListStrategy clearContainerStrategy,
         GetDirectoryStrategy getDirectoryStrategy,
         MkdirStrategy mkdirStrategy,
         CountListStrategy countBlobsStrategy,
         DeleteDirectoryStrategy rmDirStrategy) {
      super(blobBuilders, clearContainerStrategy, getDirectoryStrategy, mkdirStrategy, countBlobsStrategy, rmDirStrategy);
      this.blobBuilder = blobBuilder;
   }

   @Override
   public BlobBuilder blobBuilder() {
      return blobBuilder;
   }
}
