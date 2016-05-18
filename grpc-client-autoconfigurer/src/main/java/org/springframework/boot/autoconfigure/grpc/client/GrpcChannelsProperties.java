package org.springframework.boot.autoconfigure.grpc.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * Created by rayt on 5/17/16.
 */
@ConfigurationProperties("grpc.client")
public class GrpcChannelsProperties {
  @NestedConfigurationProperty
  private Map<String, GrpcChannelProperties> channels;

  public Map<String, GrpcChannelProperties> getChannels() {
    return channels;
  }

  public void setChannels(Map<String, GrpcChannelProperties> channels) {
    this.channels = channels;
  }

  @Override
  public String toString() {
    return "GrpcChannelsProperties{" +
        "channels=" + channels +
        '}';
  }
}
