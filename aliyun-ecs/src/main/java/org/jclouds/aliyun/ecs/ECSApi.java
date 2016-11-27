package org.jclouds.aliyun.ecs;

import java.io.Closeable;
import java.util.Set;

import com.aliyuncs.IAcsClient;

public interface ECSApi extends Closeable {

   static final String DEFAULT_REGION = "cn-hangzhou";

   IAcsClient getAcsClient(String region);

   Set<String> getAvailableRegions();

   String encodeToId(String region, String id);

   String decodeToRegion(String id);

   String decodeToId(String id);
}
