package org.jclouds.aliyun.slb.loadbalancer.strategy;

import javax.inject.Inject;

import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;

public class SLBListLoadBalancersStrategy implements ListLoadBalancersStrategy {

   protected final LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter;

   @Inject
   public SLBListLoadBalancersStrategy (
         LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter) {
      this.adapter = adapter;
   }

   @Override
   public Iterable<? extends LoadBalancerMetadata> listLoadBalancers() {
      return adapter.listLoadBalancers();
   }
}
