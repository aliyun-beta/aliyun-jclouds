package org.jclouds.aliyun.test;

import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

public class SLBTest {

   private static LoadBalancerService loadBalancerService;

   private static final String provider = "aliyun-slb";
   private static final String key = "accessKey";
   private static final String secret = "accessKeySecret";

   private static final String testRegion = "cn-hangzhou";
   private static final String testLoadBalancer = "test-load-balancer";
   private LoadBalancerMetadata lbm;

   @BeforeSuite
   public void beforeSuite() {
      BasicConfigurator.configure();
      LoadBalancerServiceContext context = ContextBuilder
            .newBuilder(provider)
            .credentials(key, secret)
            .buildView(LoadBalancerServiceContext.class);
      loadBalancerService = context.getLoadBalancerService();
   }

   @AfterSuite
   public void afterSuite() {
   }

   @BeforeTest
   public void beforeTest() {
   }

   @AfterTest
   public void afterTest() {
   }

   @BeforeClass
   public void beforeClass() {
      LocationBuilder location = new LocationBuilder()
            .scope(LocationScope.REGION)
            .id(testRegion)
            .description(testRegion);
      Set<NodeMetadata> nodes = ImmutableSet.<NodeMetadata> builder()
            .add(new NodeMetadataBuilder()
                  .status(Status.RUNNING)
                  .id("cn-hangzhou:i-233ehfkdv")
                  .build())
            .build();
      lbm = loadBalancerService
            .createLoadBalancerInLocation(
                  location.build(),
                  testLoadBalancer,
                  "http", 80, 80, nodes);
   }

   @AfterClass
   public void afterClass() {
      loadBalancerService.destroyLoadBalancer(lbm.getId());
   }

   @Test
   public void listLoadBalancersst() {
      loadBalancerService.listLoadBalancers();
   }

   @Test
   public void getLoadBalancerMetadata() {
      loadBalancerService.getLoadBalancerMetadata(lbm.getId());
   }

   @Test
   public void listAssignableLocations() {
      loadBalancerService.listAssignableLocations();
   }
}
