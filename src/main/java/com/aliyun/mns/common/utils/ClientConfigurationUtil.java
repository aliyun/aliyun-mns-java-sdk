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

package com.aliyun.mns.common.utils;

import com.aliyun.mns.common.http.ClientConfiguration;

public class ClientConfigurationUtil {
    public static void copyConfig(ClientConfiguration source, ClientConfiguration target) {
        target.setConnectionTimeout(source.getConnectionTimeout());
        target.setMaxConnections(source.getMaxConnections());
        target.setProxyDomain(source.getProxyDomain());
        target.setProxyHost(source.getProxyHost());
        target.setProxyPassword(source.getProxyPassword());
        target.setProxyPort(source.getProxyPort());
        target.setProxyUsername(source.getProxyUsername());
        target.setProxyWorkstation(source.getProxyWorkstation());
        target.setSocketTimeout(source.getSocketTimeout());
        target.setUserAgent(source.getUserAgent());
    }
}
