package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Channel;

/**
 * Created by rayt on 5/17/16.
 */
public interface GrpcChannelFactory {
  public Channel createChannel(String name);
}
