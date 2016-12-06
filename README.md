## Providers of JClouds for Aliyun

[![Build Status](https://travis-ci.org/aliyun-beta/aliyun-jclouds.svg?branch=master)](https://travis-ci.org/aliyun-beta/aliyun-jclouds)

### Introduction

aliyun-jclouds adapt for jclouds to use cloud service of [aliyun](https://www.aliyun.com)

### Providers

| Provider | Service |
|------|------|
|aliyun-ecs|ComputeService|
|aliyun-oss|BlobStore|
|aliyun-slb|LoadBalancerService|

### Maven

    <dependencies>
      <dependency>
        <groupId>io.github.aliyun-beta</groupId>
        <artifactId>aliyun-ecs</artifactId>
        <version>1.0.0</version>
      </dependency>
      <dependency>
        <groupId>io.github.aliyun-beta</groupId>
        <artifactId>aliyun-oss</artifactId>
        <version>1.0.0</version>
      </dependency>
      <dependency>
        <groupId>io.github.aliyun-beta</groupId>
        <artifactId>aliyun-slb</artifactId>
        <version>1.0.0</version>
      </dependency>
    </dependencies>

### Usage

Offical documents link [Apache-jclouds](http://jclouds.apache.org/start)

###### ComputeService

    ComputeService computeService;
    String provider = "aliyun-ecs";
    String key = "Your AccessKey";
    String secret = "Your AccessKeySecret";
    ComputeServiceContext context = ContextBuilder
          .newBuilder(provider)
          .credentials(key, secret)
          .buildView(ComputeServiceContext.class);
    computeService = context.getComputeService();

###### BlobStore

    BlobStore blobStore;
    String provider = "aliyun-oss";
    String key = "Your AccessKey";
    String secret = "Your AccessKeySecret";
    BlobStoreContext context = ContextBuilder
          .newBuilder(provider)
          .credentials(key, secret)
          .buildView(BlobStoreContext.class);
    blobStore = context.getBlobStore();

###### LoadBalancerService

    LoadBalancerService loadBalancerService;
    String provider = "aliyun-slb";
    String key = "Your AccessKey";
    String secret = "Your AccessKeySecret";
    LoadBalancerServiceContext context = ContextBuilder
          .newBuilder(provider)
          .credentials(key, secret)
          .buildView(LoadBalancerServiceContext.class);
    loadBalancerService = context.getLoadBalancerService();

### Build

> mvn package -DskipTests

### License

Licensed under the Apache License, Version 2.0
