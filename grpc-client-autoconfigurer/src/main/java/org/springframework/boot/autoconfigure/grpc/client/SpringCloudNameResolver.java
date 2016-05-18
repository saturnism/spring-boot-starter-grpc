package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.ResolvedServerInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rayt on 5/17/16.
 */
public class SpringCloudNameResolver extends NameResolver {
  private final String name;
  private final DiscoveryClient client;
  private final Attributes attributes;
  private Listener listener;

  public SpringCloudNameResolver(String name, DiscoveryClient client, Attributes attributes) {
    this.name = name;
    this.client = client;
    this.attributes = attributes;
  }
  @Override
  public String getServiceAuthority() {
    return name;
  }

  @Override
  public void start(Listener listener) {
    this.listener = listener;
    refresh();
  }

  @Override
  public void refresh() {
    List<ResolvedServerInfo> servers = new ArrayList<ResolvedServerInfo>();
    for (ServiceInstance serviceInstance : client.getInstances(name)) {
      System.out.println("Service Instance: " + serviceInstance.getHost() + ":" + serviceInstance.getPort());
      servers.add(new ResolvedServerInfo(InetSocketAddress.createUnresolved(serviceInstance.getHost(), serviceInstance.getPort()),Attributes.EMPTY));
    }
    this.listener.onUpdate(servers, Attributes.EMPTY);
  }

  @Override
  public void shutdown() {
  }
}
