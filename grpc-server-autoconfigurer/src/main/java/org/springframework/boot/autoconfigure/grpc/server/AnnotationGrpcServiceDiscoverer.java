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

package org.springframework.boot.autoconfigure.grpc.server;

import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Discovers gRPC service implementations by the {@link GrpcService} annotation.
 * @author Ray Tsang
 */
public class AnnotationGrpcServiceDiscoverer implements ApplicationContextAware, GrpcServiceDiscoverer {
  private static final Logger logger = LoggerFactory.getLogger(AnnotationGrpcServiceDiscoverer.class);

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  public Collection<String> findGrpcServiceBeanNames() {
    String[] beanNames = this.applicationContext
        .getBeanNamesForAnnotation(GrpcService.class);
    return Collections.unmodifiableList(Arrays.asList(beanNames));
  }

  @Override
  public Collection<GrpcServiceDefinition> findGrpcServices() {
    Collection<String> beanNames = findGrpcServiceBeanNames();
    List<GrpcServiceDefinition> definitions = new ArrayList<GrpcServiceDefinition>(
        beanNames.size());
    for (String beanName : beanNames) {
      Object bean = this.applicationContext.getBean(beanName);
      Class<?> beanClazz = bean.getClass();
      GrpcService grpcServiceAnnotation = AnnotationUtils.findAnnotation(beanClazz,
              GrpcService.class);
      Class<?> grpcClazz = grpcServiceAnnotation.value();
      Class<?>[] grpcInnerClasses = grpcClazz.getDeclaredClasses();
      boolean bindServiceFound = false;
      for (Class<?> clazz : grpcInnerClasses)  {
        if (! clazz.getName().endsWith("ImplBase")) {
          continue;
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
          if (method.getName().equals("bindService")) {
            bindServiceFound = true;
            try {
  //            Class<?> grpcInterfaceClazz = method.getParameterTypes()[0];
  //            if (!grpcInterfaceClazz.isAssignableFrom(beanClazz)) {
  //              throw new IllegalStateException("gRPC service class: "
  //                  + bean.getClass().getName() + " does not implement "
  //                  + grpcInterfaceClazz.getName());
  //            }
              ServerServiceDefinition definition = (ServerServiceDefinition) method
                      .invoke(bean, null);
              definitions.add(new GrpcServiceDefinition(beanName, beanClazz,
                      definition));
              logger.debug("Found gRPC service: " + definition.getServiceDescriptor().getName()
                      + ", bean: " + beanName + ", class: "
                      + bean.getClass().getName());
            } catch (IllegalAccessException e) {
              throw new IllegalStateException(e);
            } catch (IllegalArgumentException e) {
              throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
              throw new IllegalStateException(e);
            }
          }
        }
      }
      if (!bindServiceFound) {
        throw new IllegalStateException(grpcClazz.getName()
            + " does not have a static bindService method, are you sure this is a gRPC generated class?");
      }
    }
    return definitions;
  }

}
