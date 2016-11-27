package org.jclouds.aliyun.ecs.compute;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap.Factory;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class ECSComputeService extends BaseComputeService {

   private final JCloudsNativeComputeServiceAdapter client;

   @Inject
   protected ECSComputeService(
         ComputeServiceContext context,
         ECSComputeServiceAdapter client,
         Map<String, Credentials> credentialStore,
         @Memoized Supplier<Set<? extends Image>> images,
         Supplier<Set<? extends Hardware>> hardwareProfiles,
         @Memoized Supplier<Set<? extends Location>> locations,
         ListNodesStrategy listNodesStrategy,
         GetImageStrategy getImageStrategy,
         GetNodeMetadataStrategy getNodeMetadataStrategy,
         CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
         RebootNodeStrategy rebootNodeStrategy,
         DestroyNodeStrategy destroyNodeStrategy,
         ResumeNodeStrategy resumeNodeStrategy,
         SuspendNodeStrategy suspendNodeStrategy,
         @Named("ECS") Provider<TemplateBuilder> templateBuilderProvider,
         @Named("ECS") Provider<TemplateOptions> templateOptionsProvider,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
         @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
         Factory initScriptRunnerFactory,
         InitAdminAccess initAdminAccess,
         org.jclouds.compute.callables.RunScriptOnNode.Factory runScriptOnNodeFactory,
         PersistNodeCredentials persistNodeCredentials,
         Timeouts timeouts,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         Optional<ImageExtension> imageExtension,
         Optional<SecurityGroupExtension> securityGroupExtension) {
      super(context, credentialStore, images, hardwareProfiles, locations, listNodesStrategy, getImageStrategy,
            getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, resumeNodeStrategy,
            suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated, nodeSuspended,
            initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory, persistNodeCredentials, timeouts, userExecutor,
            imageExtension, securityGroupExtension);
      this.client = client;
   }

   @Override
   public Set<? extends NodeMetadata> listNodesByIds(Iterable<String> ids) {
      return ImmutableSet.<NodeMetadata> builder()
            .addAll(client.listNodesByIds(ids))
            .build();
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, Template template) throws RunNodesException {
      return super.createNodesInGroup(group, count, template);
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, TemplateOptions templateOptions) throws RunNodesException {
      return this.createNodesInGroup(group, count, templateBuilder().options(templateOptions).build());
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count) throws RunNodesException {
      return super.createNodesInGroup(group, count);
   }
}
