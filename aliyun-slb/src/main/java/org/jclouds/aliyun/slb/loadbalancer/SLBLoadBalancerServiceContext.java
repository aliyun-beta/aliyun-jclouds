package org.jclouds.aliyun.slb.loadbalancer;

import org.jclouds.aliyun.slb.loadbalancer.internal.SLBLoadBalancerServiceContextImpl;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(SLBLoadBalancerServiceContextImpl.class)
public interface SLBLoadBalancerServiceContext extends LoadBalancerServiceContext {

   @Override
   SLBLoadBalancerService getLoadBalancerService();
}
