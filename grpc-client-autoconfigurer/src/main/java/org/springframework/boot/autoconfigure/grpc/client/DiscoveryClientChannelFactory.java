package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.SimpleLoadBalancerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * Created by rayt on 5/17/16.
 */
public class DiscoveryClientChannelFactory implements GrpcChannelFactory {
  private final GrpcChannelsProperties channels;
  private final DiscoveryClient client;

  public DiscoveryClientChannelFactory(GrpcChannelsProperties channels, DiscoveryClient client) {
    this.channels = channels;
    this.client = client;
  }

  @Override
  public Channel createChannel(String name) {
    return ManagedChannelBuilder.forTarget(name)
        .loadBalancerFactory(SimpleLoadBalancerFactory.getInstance())
        .nameResolverFactory(new DiscoveryClientResolverFactory(client))
        .usePlaintext(channels.getChannels().get(name).isPlaintext()).build();
  }
}
