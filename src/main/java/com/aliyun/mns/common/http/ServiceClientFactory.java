/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.mns.common.http;

import java.util.HashMap;
import java.util.Map;

public class ServiceClientFactory {

    private static final Object lock = new Object();
    private static final Map<ClientConfiguration, ServiceClient> serviceClientMap = new HashMap<ClientConfiguration, ServiceClient>();

    public static ServiceClient createServiceClient(ClientConfiguration config) {
        if (config == null) {
            config = new ClientConfiguration();
        }
        synchronized (serviceClientMap) {
            ServiceClient serviceClient = serviceClientMap.get(config);
            if (serviceClient == null) {
                serviceClient = new DefaultServiceClient(config);
                serviceClientMap.put(config, serviceClient);
            } else {
                serviceClient.ref();
            }
            return serviceClient;
        }
    }

    public static void closeServiceClient(ServiceClient serviceClient) {
        synchronized (serviceClientMap) {
            int count = serviceClient.unRef();
            if (count == 0) {
                serviceClientMap.remove(serviceClient.getClientConfigurationNoClone());
            }
        }
    }

    public static int getServiceClientCount() {
        synchronized (serviceClientMap) {
            return serviceClientMap.size();
        }
    }
}
