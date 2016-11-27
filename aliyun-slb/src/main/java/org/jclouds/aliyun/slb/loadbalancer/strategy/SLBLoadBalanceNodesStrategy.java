package org.jclouds.aliyun.slb.loadbalancer.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.aliyun.slb.SLBApi;
import org.jclouds.aliyun.slb.loadbalancer.domain.SLBLoadBalancerProtocol;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.LoadBalancerServiceAdapter;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.slb.model.v20140515.AddBackendServersRequest;
import com.aliyuncs.slb.model.v20140515.CreateLoadBalancerHTTPListenerRequest;
import com.aliyuncs.slb.model.v20140515.CreateLoadBalancerHTTPSListenerRequest;
import com.aliyuncs.slb.model.v20140515.CreateLoadBalancerTCPListenerRequest;
import com.aliyuncs.slb.model.v20140515.StartLoadBalancerListenerRequest;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SLBLoadBalanceNodesStrategy implements LoadBalanceNodesStrategy {

   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final SLBApi api;
   protected final LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter;

   @Inject
   public SLBLoadBalanceNodesStrategy (
         LoadBalancerServiceAdapter<LoadBalancerMetadata, Location> adapter,
         SLBApi api) {
      this.adapter = adapter;
      this.api = api;
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(
         Location location,
         String name,
         String protocol,
         int loadBalancerPort,
         int instancePort,
         Iterable<? extends NodeMetadata> nodes) {
      IAcsClient client = api.getAcsClient(location.getId());
      LoadBalancerMetadata lbm = adapter
            .createLoadBalancerInLocation(location, name, protocol, loadBalancerPort, instancePort, nodes);
      String loadBalancerId = lbm.getId();
      if (SLBLoadBalancerProtocol.HTTP.name().equalsIgnoreCase(protocol)) {
         this.createLoadBalancerHTTPListener(client, loadBalancerId, loadBalancerPort, instancePort);
      } else if (SLBLoadBalancerProtocol.HTTPS.name().equals(protocol)) {
         this.createLoadBalancerHTTPSListener(client, loadBalancerId, loadBalancerPort, instancePort);
      } else if (SLBLoadBalancerProtocol.TCP.name().equals(protocol)) {
         this.createLoadBalancerTCPListener(client, loadBalancerId, loadBalancerPort, instancePort);
      } else if (SLBLoadBalancerProtocol.UDP.name().equals(protocol)) {
         this.createLoadBalancerUDPListener(client, loadBalancerId, loadBalancerPort, instancePort);
      } else {
         logger.warn("unrecognized protocol");
      }
      this.addBackendServers(client, loadBalancerId, nodes);
      return lbm;
   }

   private void createLoadBalancerHTTPListener(
         IAcsClient client,
         String loadBalancerId,
         int loadBalancerPort,
         int instancePort) {
      CreateLoadBalancerHTTPListenerRequest req = new CreateLoadBalancerHTTPListenerRequest();
      req.setLoadBalancerId(loadBalancerId);
      req.setListenerPort(loadBalancerPort);
      req.setBackendServerPort(instancePort);
      req.setBandwidth(-1);
      req.setStickySession("off");
      req.setHealthCheck("on");
      req.setHealthCheckURI("/");
      req.setHealthCheckTimeout(5);
      req.setHealthCheckInterval(2);
      req.setHealthyThreshold(3);
      req.setUnhealthyThreshold(3);
      StartLoadBalancerListenerRequest slblreq = new StartLoadBalancerListenerRequest();
      slblreq.setLoadBalancerId(loadBalancerId);
      slblreq.setListenerPort(loadBalancerPort);
      try {
         client.getAcsResponse(req);
         client.getAcsResponse(slblreq);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   private void createLoadBalancerHTTPSListener(
         IAcsClient client,
         String loadBalancerId,
         int loadBalancerPort,
         int instancePort) {
      CreateLoadBalancerHTTPSListenerRequest req = new CreateLoadBalancerHTTPSListenerRequest();
      req.setLoadBalancerId(loadBalancerId);
      req.setListenerPort(loadBalancerPort);
      req.setBackendServerPort(instancePort);
      req.setBandwidth(-1);
      req.setStickySession("off");
      req.setHealthCheck("on");
      req.setHealthCheckURI("/");
      req.setHealthCheckTimeout(5);
      req.setHealthCheckInterval(2);
      req.setHealthyThreshold(3);
      req.setUnhealthyThreshold(3);
      StartLoadBalancerListenerRequest slblreq = new StartLoadBalancerListenerRequest();
      slblreq.setLoadBalancerId(loadBalancerId);
      slblreq.setListenerPort(loadBalancerPort);
      try {
         client.getAcsResponse(req);
         client.getAcsResponse(slblreq);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   private void createLoadBalancerTCPListener(
         IAcsClient client,
         String loadBalancerId,
         int loadBalancerPort,
         int instancePort) {
      CreateLoadBalancerTCPListenerRequest req = new CreateLoadBalancerTCPListenerRequest();
      req.setLoadBalancerId(loadBalancerId);
      req.setListenerPort(loadBalancerPort);
      req.setBackendServerPort(instancePort);
      req.setBandwidth(-1);
      req.setHealthCheckConnectPort(instancePort);
      req.setHealthCheckConnectTimeout(5);
      req.setHealthCheckInterval(2);
      req.setHealthyThreshold(3);
      req.setUnhealthyThreshold(3);
      StartLoadBalancerListenerRequest slblreq = new StartLoadBalancerListenerRequest();
      slblreq.setLoadBalancerId(loadBalancerId);
      slblreq.setListenerPort(loadBalancerPort);
      try {
         client.getAcsResponse(req);
         client.getAcsResponse(slblreq);
      } catch (Exception e) {
         logger.warn(e.getMessage());
      }
   }

   private void createLoadBalancerUDPListener(
         IAcsClient client,
         String loadBalancerId,
         int loadBalancerPort,
         int instancePort) {
   }

   private void addBackendServers(
         IAcsClient client,
         String loadBalancerId,
         Iterable<? extends NodeMetadata> nodes) {
      Gson gson = new GsonBuilder().create();
      Iterator<? extends NodeMetadata> it = nodes.iterator();
      ImmutableSet.Builder<String> builder = ImmutableSet.<String> builder();
      List<Map<String, String>> servers = new ArrayList<Map<String, String>>();
      int index = 0;
      while (it.hasNext()) {
         NodeMetadata nm = it.next();
         Map<String, String> map = new HashMap<String, String>();
         map.put("ServerId", api.decodeToId(nm.getId()));
         servers.add(map);
         index++;
         if (index == 20) {
            builder.add(gson.toJson(servers));
            servers.clear();
         }
      }
      builder.add(gson.toJson(servers));
      Set<String> serverGroups = builder.build();
      AddBackendServersRequest req = new AddBackendServersRequest();
      req.setLoadBalancerId(loadBalancerId);
      for (String group : serverGroups) {
         req.setBackendServers(group);
         try {
            client.getAcsResponse(req);
         } catch (Exception e) {
            logger.warn(e.getMessage());
         }
      }
   }
}
