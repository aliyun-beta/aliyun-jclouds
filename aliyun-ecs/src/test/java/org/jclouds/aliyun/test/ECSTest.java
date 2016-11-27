package org.jclouds.aliyun.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

public class ECSTest {

   private static ComputeService computeService;

   private static final String provider = "aliyun-ecs";
   private static final String key = "accessKey";
   private static final String secret = "accessKeySecret";

   private static final String testRegion = "cn-beijing";

   @BeforeSuite
   public void beforeSuite() {
      BasicConfigurator.configure();
      ComputeServiceContext context = ContextBuilder
            .newBuilder(provider)
            .credentials(key, secret)
            .buildView(ComputeServiceContext.class);
      computeService = context.getComputeService();
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
   }

   @AfterClass
   public void afterClass() {
   }

   @Test
   public void createAndStartAndStopAndDestroy() {
      Image image = computeService.getImage("cn-beijing:aliyun1501_64_20G_aliaegis_20150325.vhd");
      Hardware hardware = new HardwareBuilder()
            .id("ecs.t1.small")
            .build();
      Location location = new LocationBuilder()
            .scope(LocationScope.ZONE)
            .id(testRegion)
            .description(testRegion)
            .build();
      TemplateOptions options = new TemplateOptions();
      options.blockUntilRunning(false);
      options.blockOnComplete(false);
      Template template = new TemplateImpl(image, hardware, location, options);
      Set<? extends NodeMetadata> set = null;
      try {
         set = computeService.createNodesInGroup(testRegion, 1, template);
      } catch (RunNodesException e) {
         set = ImmutableSet.<NodeMetadata> builder().build();
      }
      Set<String> nodeIds = new HashSet<String>();
      for (NodeMetadata node : set) {
         nodeIds.add(node.getId());
      }
      computeService.listNodesByIds(nodeIds);
      for (NodeMetadata node : set) {
         computeService.resumeNode(node.getId());
         computeService.rebootNode(node.getId());
         computeService.suspendNode(node.getId());
         computeService.destroyNode(node.getId());
      }
   }

   @Test
   public void resumeNode() {
      computeService.listNodes();
   }

   @Test
   public void listNodes() {
      computeService.listNodes();
   }

   @Test
   public void listImages() {
      computeService.listImages();
   }

   @Test
   public void listHardwareProfiles() {
      computeService.listHardwareProfiles();
   }

   @Test
   public void listAssignableLocations() {
      computeService.listAssignableLocations();
   }
}
