package org.apache.jclouds.slb.loadbalancer.config;

import java.util.Map;

import javax.inject.Singleton;

import org.apache.jclouds.slb.SLBApi;
import org.apache.jclouds.slb.loadbalancer.SLBLoadBalancerService;
import org.apache.jclouds.slb.loadbalancer.SLBLoadBalancerServiceAdapter;
import org.apache.jclouds.slb.loadbalancer.internal.SLBApiImpl;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.config.BaseLoadBalancerServiceContextModule;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class SLBLoadBalancerServiceContextModule extends BaseLoadBalancerServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<LoadBalancerServiceAdapter<LoadBalancerMetadata, Location>>(){}).to(SLBLoadBalancerServiceAdapter.class);
      bind(SLBApi.class).to(SLBApiImpl.class);
      bind(LoadBalancerService.class).to(SLBLoadBalancerService.class);
      install(new SLBBindLoadBalancerStrategiesByClass());
   }

   @Provides
   @Singleton
   protected final Map<String, LoadBalancerType> loadBalancerTypeProvides() {
      return ImmutableMap.<String, LoadBalancerType> builder()
            .put("internet", LoadBalancerType.GSLB)
            .put("intranet", LoadBalancerType.LB)
            .build();
   }
}
