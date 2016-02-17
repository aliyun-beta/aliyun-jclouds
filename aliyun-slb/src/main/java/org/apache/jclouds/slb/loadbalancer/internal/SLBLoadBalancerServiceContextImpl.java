package org.apache.jclouds.slb.loadbalancer.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.jclouds.slb.loadbalancer.SLBLoadBalancerService;
import org.apache.jclouds.slb.loadbalancer.SLBLoadBalancerServiceContext;
import org.jclouds.Context;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.internal.LoadBalancerServiceContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.Utils;

import com.google.common.reflect.TypeToken;

@Singleton
public class SLBLoadBalancerServiceContextImpl extends LoadBalancerServiceContextImpl implements SLBLoadBalancerServiceContext {

   @Inject
   public SLBLoadBalancerServiceContextImpl(
         @Provider Context backend,
         @Provider TypeToken<? extends Context> backendType,
         LoadBalancerService loadBalancerService,
         Utils utils) {
      super(backend, backendType, loadBalancerService, utils);
   }

   @Override
   public SLBLoadBalancerService getLoadBalancerService() {
      return SLBLoadBalancerService.class.cast(super.getLoadBalancerService());
   }
}
