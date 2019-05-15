/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.boot.autoconfigure.grpc.client;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rayt on 5/17/16.
 */
public class DiscoveryClientNameResolver extends NameResolver {
	private final String name;
	private final DiscoveryClient client;
	private final Attributes attributes;
	private Listener listener;

	public DiscoveryClientNameResolver(String name, DiscoveryClient client, Attributes attributes) {
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
		List<EquivalentAddressGroup> servers = new ArrayList<>();
		for (ServiceInstance serviceInstance : client.getInstances(name)) {
			servers.add(new EquivalentAddressGroup(new InetSocketAddress(serviceInstance.getHost(), serviceInstance.getPort())));
		}
		this.listener.onAddresses(servers, Attributes.EMPTY);
	}

	@Override
	public void shutdown() {
	}
}
