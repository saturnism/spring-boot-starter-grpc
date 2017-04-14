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
import org.springframework.cloud.client.discovery.DiscoveryClient;


import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

/**
 * Created by rayt on 5/17/16.
 */
public class DiscoveryClientResolverFactory extends NameResolver.Factory {
  private final DiscoveryClient client;
  private final Map<String, NameResolver> nameResolverMap = new ConcurrentHashMap<>();

  public DiscoveryClientResolverFactory(DiscoveryClient client) {
    this.client = client;
  }

  @Nullable
  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
  	String key = targetUri.getPath() + params.toString();
  	NameResolver resolver = nameResolverMap.get(key);
  	if (resolver != null) {
  		return resolver;
		}
  	synchronized (nameResolverMap) {
			resolver = nameResolverMap.get(key);
			if (resolver != null) {
				return resolver;
			}
			resolver = new DiscoveryClientNameResolver(targetUri.toString(), client, params);
			nameResolverMap.put(key, resolver);
		}
    return resolver;
  }

  @Override
  public String getDefaultScheme() {
    return "spring";
  }
}
