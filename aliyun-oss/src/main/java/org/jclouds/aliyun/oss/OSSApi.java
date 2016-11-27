package org.jclouds.aliyun.oss;

import java.io.Closeable;
import java.util.Set;

import com.aliyun.oss.OSS;

public interface OSSApi extends Closeable {

   static final String DEFAULT_REGION = "oss-cn-hangzhou";

   OSS getOSSClient(String region);

   Set<String> getAvailableRegions();
}
