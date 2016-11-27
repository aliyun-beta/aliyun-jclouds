package org.jclouds.aliyun.ecs.compute;

import org.jclouds.aliyun.ecs.compute.internal.ECSComputeServiceContextImpl;
import org.jclouds.compute.ComputeServiceContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(ECSComputeServiceContextImpl.class)
public interface ECSComputeServiceContext extends ComputeServiceContext {

   @Override
   ECSComputeService getComputeService();
}
