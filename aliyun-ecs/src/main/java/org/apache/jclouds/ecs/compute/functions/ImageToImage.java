package org.apache.jclouds.ecs.compute.functions;

import org.apache.jclouds.ecs.ECSApi;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;

import com.aliyuncs.ecs.model.v20140526.DescribeImagesResponse;
import com.google.common.base.Function;

public class ImageToImage implements Function<DescribeImagesResponse.Image, Image> {

   protected final ECSApi api;
   protected final String region;

   public ImageToImage(ECSApi api, String region) {
      this.api = api;
      this.region = region;
   }

   @Override
   public Image apply(DescribeImagesResponse.Image input) {
      ImageBuilder builder = new ImageBuilder();
      builder.id(api.encodeToId(region, input.getImageId()));
      builder.name(input.getImageName());
      builder.description(input.getDescription());
      OperatingSystem os = OperatingSystem.builder()
            .arch(input.getArchitecture().getStringValue())
            .description(input.getOSName())
            .build();
      builder.operatingSystem(os);
      builder.status(Image.Status.AVAILABLE);
      return builder.build();
   }
}
