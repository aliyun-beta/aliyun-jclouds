package org.jclouds.aliyun.ecs.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aliyun.ecs.ECSApi;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse.Region;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class ECSApiImpl implements ECSApi {

   private final String identity;
   private final String credential;

   @Inject
   public ECSApiImpl(@Provider Supplier<Credentials> creds) {
      this.identity = creds.get().identity;
      this.credential = creds.get().credential;
   }

   @Override
   public IAcsClient getAcsClient(String region) {
      IClientProfile profile = DefaultProfile.getProfile(region, identity, credential);
      return new DefaultAcsClient(profile);
   }

   @Override
   public Set<String> getAvailableRegions() {
      IAcsClient client = this.getAcsClient(ECSApi.DEFAULT_REGION);
      List<String> result = new ArrayList<String>();
      DescribeRegionsRequest req = new DescribeRegionsRequest();
      try {
         DescribeRegionsResponse resp = client.getAcsResponse(req);
         for (Region region : resp.getRegions()) {
            result.add(region.getRegionId());
         }
      } catch (Exception e) {
      }
      return ImmutableSet.<String> builder().addAll(result).build();
   }

   @Override
   public String encodeToId(String region, String id) {
      return checkNotNull(region, "region") + ":" + checkNotNull(id, "id");
   }

   @Override
   public String decodeToRegion(String id) {
      Iterable<String> it = Splitter.on(":").split(checkNotNull(id, "string"));
      checkArgument(Iterables.size(it) == 2, "string must be in format region:id");
      return Iterables.get(it, 0);
   }

   @Override
   public String decodeToId(String id) {
      Iterable<String> it = Splitter.on(":").split(checkNotNull(id, "string"));
      checkArgument(Iterables.size(it) == 2, "string must be in format region:id");
      return Iterables.get(it, 1);
   }

   @Override
   public void close() throws IOException {}
}
