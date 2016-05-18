package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by rayt on 5/17/16.
 */
public class AddressChannelFactory implements GrpcChannelFactory {
  private final GrpcChannelsProperties channels;
  public AddressChannelFactory(GrpcChannelsProperties channels) {
    this.channels = channels;
  }

  @Override
  public Channel createChannel(String name) {
    GrpcChannelProperties channel = channels.getChannels().get(name);
    return ManagedChannelBuilder.
        forAddress(channel.getHost(), channel.getPort()).
        usePlaintext(channel.isPlaintext()).build();
  }
}
