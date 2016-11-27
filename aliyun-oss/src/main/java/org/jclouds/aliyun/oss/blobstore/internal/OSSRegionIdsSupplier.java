package org.jclouds.aliyun.oss.blobstore.internal;

import java.util.Set;

import org.jclouds.location.suppliers.RegionIdsSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

public class OSSRegionIdsSupplier implements RegionIdsSupplier {

   private final Supplier<Set<String>> getRegions() {
      Set<String> regions = ImmutableSet.<String>builder()
            .add("oss-cn-hangzhou")
            .add("oss-cn-qingdao")
            .add("oss-cn-beijing")
            .add("oss-cn-hongkong")
            .add("oss-cn-shenzhen")
            .add("oss-cn-shanghai")
            .add("oss-us-west-1")
            .add("oss-ap-southeast-1")
            .build();
      return Suppliers.ofInstance(regions);
   }

   @Override
   public Set<String> get() {
      return this.getRegions().get();
   }
}
