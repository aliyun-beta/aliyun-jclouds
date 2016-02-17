package org.apache.jclouds.ecs.compute.functions;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Function;

public class RegionToLocation implements Function<String, Location> {

   @Override
   public Location apply(String input) {
      LocationBuilder builder = new LocationBuilder();
      builder.scope(LocationScope.REGION);
      builder.id(input);
      builder.description(input);
      return builder.build();
   }
}
