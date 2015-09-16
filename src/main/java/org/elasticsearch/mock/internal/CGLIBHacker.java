package org.elasticsearch.mock.internal;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.creation.MockitoMethodProxy;
import org.mockito.internal.creation.cglib.MockitoNamingPolicy;

/** Replacement for CGLIBHacker with AccessController calls (for method interception) */
public class CGLIBHacker implements Serializable {

  private static final long serialVersionUID = -4389233991416356668L;

  public void setMockitoNamingPolicy(MockitoMethodProxy mockitoMethodProxy) {
      try {
          MethodProxy methodProxy = mockitoMethodProxy.getMethodProxy();
          final Field createInfoField = reflectOnCreateInfo(methodProxy);
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
              createInfoField.setAccessible(true);
              return null;
            }
          });
          Object createInfo = createInfoField.get(methodProxy);
          final Field namingPolicyField = createInfo.getClass().getDeclaredField("namingPolicy");
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
              namingPolicyField.setAccessible(true);
              return null;
            }
          });
          if (namingPolicyField.get(createInfo) == null) {
              namingPolicyField.set(createInfo, MockitoNamingPolicy.INSTANCE);
          }
      } catch (Exception e) {
          throw new RuntimeException(
                          "Unable to set MockitoNamingPolicy on cglib generator which creates FastClasses", e);
      }
  }

  @SuppressWarnings("unchecked")
  private Field reflectOnCreateInfo(MethodProxy methodProxy) throws SecurityException, NoSuchFieldException {

      Class cglibMethodProxyClass = methodProxy.getClass();
      // in case methodProxy was extended by user, let's traverse the object
      // graph to find the cglib methodProxy
      // with all the fields we would like to change
      while (cglibMethodProxyClass != MethodProxy.class) {
          cglibMethodProxyClass = methodProxy.getClass().getSuperclass();
      }
      return cglibMethodProxyClass.getDeclaredField("createInfo");
  }
}
