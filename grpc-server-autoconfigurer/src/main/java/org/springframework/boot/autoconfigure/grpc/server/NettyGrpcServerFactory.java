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
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServiceDescriptor;
import io.grpc.netty.NettyServerBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * Creates a Netty gRPC server using {@link NettyServerBuilder}.
 * @author Ray Tsang
 */
public class NettyGrpcServerFactory implements GrpcServerFactory {
	private static final Log logger = LogFactory.getLog(NettyGrpcServerFactory.class);

	private final GrpcServerProperties properties;
	private final GrpcServiceDiscoverer discoverer;

	public NettyGrpcServerFactory(GrpcServerProperties properties, GrpcServiceDiscoverer discoverer) {
		this.properties = properties;
		this.discoverer = discoverer;
	}

	@Override
	public Server createServer() {
		NettyServerBuilder builder = NettyServerBuilder.forAddress(
				new InetSocketAddress(InetAddresses.forString(getAddress()), getPort()));
		Collection<GrpcServiceDefinition> definitions = discoverer.findGrpcServices();
		for (GrpcServiceDefinition definition : definitions) {
			ServiceDescriptor descriptor = definition.getService().bindService().getServiceDescriptor();
			logger.info("Registered gRPC service: " + descriptor.getName()
					+ ", bean: " + definition.getBeanName() + ", class: "
					+ definition.getService().getClass().getName());
			builder.addService(definition.getService());
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
}
