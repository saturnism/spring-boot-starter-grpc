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
import io.grpc.NameResolver;
import io.grpc.ResolvedServerInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rayt on 5/17/16.
 */
public class DiscoveryClientNameResolver extends NameResolver {
	private final String name;
	private final DiscoveryClient client;
	private final Attributes attributes;
	private Listener listener;
	private Timer refresh = new Timer();
	private AtomicBoolean refresherInit = new AtomicBoolean(false);

	private class Refresher extends TimerTask {
    @Override
    public void run() {
      refresh();
    }
  }

	private void initRefresher() {
	  if (refresherInit.compareAndSet(false, true)) {
      Refresher refresher = new Refresher();
	    this.refresh.schedule(refresher, 20000L, 20000L);
    }
  }

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
    initRefresher();
	}

  @Scheduled(fixedRate = 20000)
  @Override
  public void refresh() {
    System.out.println("update service instance info ###############################################");
    List<ResolvedServerInfo> servers = new ArrayList<>();
    for (ServiceInstance serviceInstance : client.getInstances(name)) {
      System.out.println("Service Instance: " + serviceInstance.getHost() + ":" + serviceInstance.getPort());
      servers.add(new ResolvedServerInfo(InetSocketAddress.createUnresolved(serviceInstance.getHost(), serviceInstance.getPort()),Attributes.EMPTY));
    }
    List<List<ResolvedServerInfo>> serversList = new ArrayList<>();
    for (ResolvedServerInfo info : servers) {
      List<ResolvedServerInfo> list = new ArrayList<>();
      list.add(info);
      serversList.add(list);
    }
    this.listener.onUpdate(serversList, Attributes.EMPTY);
  }

	@Override
	public void shutdown() {
	}

}
