package org.jclouds.aliyun.slb.loadbalancer.config;

import org.jclouds.aliyun.slb.loadbalancer.strategy.SLBDestroyLoadBalancerStrategy;
import org.jclouds.aliyun.slb.loadbalancer.strategy.SLBGetLoadBalancerMetadataStrategy;
import org.jclouds.aliyun.slb.loadbalancer.strategy.SLBListLoadBalancersStrategy;
import org.jclouds.aliyun.slb.loadbalancer.strategy.SLBLoadBalanceNodesStrategy;
import org.jclouds.loadbalancer.config.BindLoadBalancerStrategiesByClass;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;

public class SLBBindLoadBalancerStrategiesByClass extends BindLoadBalancerStrategiesByClass {

   @Override
   protected Class<? extends LoadBalanceNodesStrategy> defineLoadBalanceNodesStrategy() {
      return SLBLoadBalanceNodesStrategy.class;
   }

   @Override
   protected Class<? extends DestroyLoadBalancerStrategy> defineDestroyLoadBalancerStrategy() {
      return SLBDestroyLoadBalancerStrategy.class;
   }

   @Override
   protected Class<? extends GetLoadBalancerMetadataStrategy> defineGetLoadBalancerMetadataStrategy() {
      return SLBGetLoadBalancerMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListLoadBalancersStrategy> defineListLoadBalancersStrategy() {
      return SLBListLoadBalancersStrategy.class;
   }
}
