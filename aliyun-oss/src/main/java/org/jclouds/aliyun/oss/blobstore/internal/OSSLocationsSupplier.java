package org.jclouds.aliyun.oss.blobstore.internal;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class OSSLocationsSupplier implements LocationsSupplier {

   protected final RegionIdsSupplier regions;

   @Inject
   public OSSLocationsSupplier(RegionIdsSupplier regions) {
      this.regions = regions;
   }

   @Override
   public Set<? extends Location> get() {
      Builder<Location> locations = ImmutableSet.builder();
      for (String region : regions.get()) {
         LocationBuilder builder = new LocationBuilder()
               .scope(LocationScope.REGION)
               .id(region)
               .description(region);
         locations.add(builder.build());
      }
      return locations.build();
   }
}
