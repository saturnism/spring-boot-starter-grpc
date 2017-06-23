package org.springframework.boot.autoconfigure.grpc.server;

import io.grpc.BindableService;

/**
 * Created by rayt on 6/23/17.
 */
public class GrpcServiceDefinition {
  private final String beanName;
  private final BindableService service;

  public GrpcServiceDefinition(String beanName, BindableService service) {
    this.beanName = beanName;
    this.service = service;
  }

  public String getBeanName() {
    return beanName;
  }

  public BindableService getService() {
    return service;
  }
}
