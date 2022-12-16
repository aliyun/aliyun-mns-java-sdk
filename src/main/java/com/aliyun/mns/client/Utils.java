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

package com.aliyun.mns.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static URI getHttpURI(String endpoint) {
        if (endpoint == null) {
            logger.warn("参数endpoint为空指针。");
            throw new NullPointerException("参数endpoint为空指针。");
        }

        try {
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                logger.warn("仅支持http协议。Endpoint必须以http://或https://开头。");
                throw new IllegalArgumentException(
                    "仅支持http协议。Endpoint必须以http://或https://开头。");
            }
            while (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }

            if (endpoint.length() < "http://".length()) {
                logger.warn("参数endpoint地址无效.");
                throw new IllegalArgumentException("参数endpoint地址无效.");
            }
            return new URI(endpoint);

        } catch (URISyntaxException e) {
            logger.warn("uri syntax error");
            throw new IllegalArgumentException(e);
        }
    }
}
