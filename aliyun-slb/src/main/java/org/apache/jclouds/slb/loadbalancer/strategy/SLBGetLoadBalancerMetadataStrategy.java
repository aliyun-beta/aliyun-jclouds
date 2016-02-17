package org.apache.jclouds.slb.loadbalancer.strategy;

import javax.inject.Inject;

import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;

public class SLBGetLoadBalancerMetadataStrategy implements GetLoadBalancerMetadataStrategy {

   protected final LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter;

   @Inject
   public SLBGetLoadBalancerMetadataStrategy (
         LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter) {
      this.adapter = adapter;
   }

   @Override
   public LoadBalancerMetadata getLoadBalancer(String id) {
      return adapter.getLoadBalancer(id);
   }
}
