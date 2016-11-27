package org.jclouds.aliyun.slb.loadbalancer;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.internal.BaseLoadBalancerService;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

@Singleton
public class SLBLoadBalancerService extends BaseLoadBalancerService {

   protected final LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter;

   @Inject
   protected SLBLoadBalancerService(
         LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter,
         LoadBalancerServiceContext context,
         Supplier<Location> defaultLocationSupplier,
         @Memoized Supplier<Set<? extends Location>> locations,
         LoadBalanceNodesStrategy loadBalancerStrategy,
         GetLoadBalancerMetadataStrategy getLoadBalancerMetadataStrategy,
         DestroyLoadBalancerStrategy destroyLoadBalancerStrategy,
         ListLoadBalancersStrategy listLoadBalancersStrategy) {
      super(defaultLocationSupplier, context, loadBalancerStrategy,
            getLoadBalancerMetadataStrategy, destroyLoadBalancerStrategy,
            listLoadBalancersStrategy, locations);
      this.adapter = adapter;
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return ImmutableSet.<Location> builder()
            .addAll(adapter.listAssignableLocations())
            .build();
   }
}
