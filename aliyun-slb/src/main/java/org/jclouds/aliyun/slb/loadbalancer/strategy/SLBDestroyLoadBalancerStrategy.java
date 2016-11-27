package org.jclouds.aliyun.slb.loadbalancer.strategy;

import javax.inject.Inject;

import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;

public class SLBDestroyLoadBalancerStrategy implements DestroyLoadBalancerStrategy {

   protected final LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter;

   @Inject
   public SLBDestroyLoadBalancerStrategy (
         LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter) {
      this.adapter = adapter;
   }

   @Override
   public LoadBalancerMetadata destroyLoadBalancer(String id) {
      adapter.destroyLoadBalancer(id);
      return null;
   }
}
