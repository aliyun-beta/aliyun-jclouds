package org.apache.jclouds.ecs.compute.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.jclouds.ecs.compute.ECSComputeService;
import org.apache.jclouds.ecs.compute.ECSComputeServiceContext;
import org.jclouds.Context;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;

@Singleton
public class ECSComputeServiceContextImpl extends ComputeServiceContextImpl implements ECSComputeServiceContext {

   @Inject
   public ECSComputeServiceContextImpl(
         @Provider Context backend,
         @Provider TypeToken<? extends Context> backendType,
         ComputeService computeService,
         Utils utils) {
      super(backend, backendType, computeService, utils);
   }

   @Override
   public ECSComputeService getComputeService() {
      return ECSComputeService.class.cast(super.getComputeService());
   }
}
