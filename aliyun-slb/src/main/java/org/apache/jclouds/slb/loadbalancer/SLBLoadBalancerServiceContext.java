package org.apache.jclouds.slb.loadbalancer;

import org.apache.jclouds.slb.loadbalancer.internal.SLBLoadBalancerServiceContextImpl;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(SLBLoadBalancerServiceContextImpl.class)
public interface SLBLoadBalancerServiceContext extends LoadBalancerServiceContext {

   @Override
   SLBLoadBalancerService getLoadBalancerService();
}
