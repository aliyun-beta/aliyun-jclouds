package org.apache.jclouds.slb.loadbalancer.functions;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.logging.Logger;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancerAttributeRequest;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancerAttributeResponse;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancersResponse.LoadBalancer;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class LoadBalancerToLoadBalancerMetadata implements Function<LoadBalancer, LoadBalancerMetadata> {

   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final IAcsClient client;
   protected final Map<String, LoadBalancerType> loadBalancerTypes;

   public LoadBalancerToLoadBalancerMetadata(
         IAcsClient client,
         Map<String, LoadBalancerType> loadBalancerTypes) {
      this.client = client;
      this.loadBalancerTypes = loadBalancerTypes;
   }

   @Override
   public LoadBalancerMetadata apply(LoadBalancer input) {
      DescribeLoadBalancerAttributeRequest req = new DescribeLoadBalancerAttributeRequest();
      req.setLoadBalancerId(input.getLoadBalancerId());
      DescribeLoadBalancerAttributeResponse resp = null;
      try {
         resp = client.getAcsResponse(req);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
      LocationBuilder location = new LocationBuilder()
            .scope(LocationScope.REGION)
            .id(resp.getRegionId())
            .description(resp.getRegionId());
      return new LoadBalancerMetadataImpl(
            loadBalancerTypes.get(resp.getAddressType()),
            input.getLoadBalancerId(),
            input.getLoadBalancerName(),
            input.getLoadBalancerId(),
            location.build(),
            null,
            ImmutableMap.<String, String>builder().build(),
            ImmutableSet.<String> builder().add(resp.getAddress()).build());
   }
}
