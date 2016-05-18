package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Created by rayt on 5/17/16.
 */
public class SpringCloudNameResolverFactory extends NameResolver.Factory {
  private final DiscoveryClient client;

  public SpringCloudNameResolverFactory(DiscoveryClient client) {
    this.client = client;
  }

  @Nullable
  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new SpringCloudNameResolver(targetUri.toString(), client, params);
  }

  @Override
  public String getDefaultScheme() {
    return "spring";
  }
}
