/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.grpc.server;

import com.google.common.net.InetAddresses;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates a Netty gRPC server using {@link NettyServerBuilder}.
 * @author Ray Tsang
 */
public class NettyGrpcServerFactory implements GrpcServerFactory {
  private static final Logger logger = LoggerFactory.getLogger(NettyGrpcServerFactory.class);

  private final GrpcServerProperties properties;
  private final List<GrpcServiceDefinition> services = new LinkedList<GrpcServiceDefinition>();

  public NettyGrpcServerFactory(GrpcServerProperties properties) {
    this.properties = properties;
  }

  @Override
  public Server createServer() {
    NettyServerBuilder builder = NettyServerBuilder.forAddress(
        new InetSocketAddress(InetAddresses.forString(getAddress()), getPort()));
    for (GrpcServiceDefinition service : this.services) {
      logger.info("Registered gRPC service: " + service.getDefinition().getServiceDescriptor().getName()
          + ", bean: " + service.getBeanName() + ", class: "
          + service.getBeanClazz().getName());
      builder.addService(service.getDefinition());
    }

    return builder.build();
  }

  @Override
  public String getAddress() {
    return this.properties.getAddress();
  }

  @Override
  public int getPort() {
    return this.properties.getPort();
  }

  @Override
  public void addService(GrpcServiceDefinition service) {
    this.services.add(service);
  }

}
