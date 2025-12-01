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

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final Gson GSON_INSTANCE = new Gson();

    /**
     * 对接入点域名进行校验确保包含 http 或 https 协议头，然后返回去掉末尾 / 的域名
     * <p>eg：参数为 http://1202283709788407.mns.cn-hangzhou.aliyuncs.com/ 则返回 http://1202283709788407.mns.cn-hangzhou.aliyuncs.com
     */
    public static URI getHttpURI(String endpoint) {
        if (endpoint == null) {
            logger.warn("The endpoint parameter is null.");
            throw new NullPointerException("The endpoint parameter is null.");
        }

        try {
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                logger.warn("Only HTTP protocol is supported. The endpoint must start with 'http://' or 'https://'.");
                throw new IllegalArgumentException(
                    "Only HTTP protocol is supported. The endpoint must start with 'http://' or 'https://'.");
            }
            while (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }

            URI uri = new URI(endpoint);

            // 校验主机名是否为空
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                throw new IllegalArgumentException("Invalid URI: Host is missing.");
            }

            return uri;
        } catch (URISyntaxException e) {
            logger.warn("URI syntax error:" + e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public static String toJsonString(boolean emptyCheck, Object object) {
        String json = GSON_INSTANCE.toJson(object);
        if (emptyCheck && (StringUtils.isBlank(json) || json.equals("{}"))) {
            throw new IllegalArgumentException("json string is empty");
        }
        return json;
    }

    public static InputStream toJsonInputStream(boolean emptyCheck, Object object) {
        String jsonString = toJsonString(emptyCheck, object);
        return new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return GSON_INSTANCE.fromJson(jsonStr, clazz);
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        String jsonStr = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining());
        return GSON_INSTANCE.fromJson(jsonStr, clazz);
    }
}
