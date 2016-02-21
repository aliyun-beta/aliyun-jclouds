Providers of JClouds for Aliyun

[![Build Status](https://travis-ci.org/aliyun-beta/aliyun-jclouds.svg?branch=master)](https://travis-ci.org/aliyun-beta/aliyun-jclouds)

#### *modules*
* aliyun-ecs **jclouds-compute**
* aliyun-oss **jclouds-blobstore**
* aliyun-slb **jclouds-loadbalancer**

| Name | Type |
|------|------|
|aliyun-ecs|provider|
|aliyun-oss|provider|
|aliyun-slb|provider|

#### *compile*
Providers use **Google Auto** to generate services

**It is important to do like this**
> mvn clean compile

Then you will find an folder named services from target\classes\META-INF in each modules

If you do not do the operation, an error will happends.

    key [aliyun-ecs] not in the list of providers or apis

#### *test*
Please change the accessKey and secret witch generate by your own account of [Aliyun](http://www.aliyun.com) in each test classes before test

    private static final String key = "nyCUviU59zdxOIYJ";
    private static final String secret = "VX99Yry885o0lpjFgzIpjibrVJIGgB";

Then run command
> mvn test

#### *changelog*
| Date | Message |
|------|---------|
|2016-01-17|push for checking|
|2016-01-25|fix some bugs|

#### License
Licensed under the Apache License, Version 2.0
