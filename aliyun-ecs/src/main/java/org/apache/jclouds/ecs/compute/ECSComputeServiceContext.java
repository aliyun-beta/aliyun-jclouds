package org.apache.jclouds.ecs.compute;

import org.apache.jclouds.ecs.compute.internal.ECSComputeServiceContextImpl;
import org.jclouds.compute.ComputeServiceContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(ECSComputeServiceContextImpl.class)
public interface ECSComputeServiceContext extends ComputeServiceContext {

   @Override
   ECSComputeService getComputeService();
}
