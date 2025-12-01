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

import com.aliyun.mns.common.HttpMethod;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.mns.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.mns.common.utils.CodingUtils.assertStringNotEmpty;

/**
 * 表示发送请求的信息。
 */
public class RequestMessage extends HttpMesssage {
    private HttpMethod method = HttpMethod.GET; // HTTP Method. default GET.
    private URI endpoint;
    private String resourcePath;
    private final Date requestDateTime;
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * 构造函数。
     */
    public RequestMessage() {
        this.requestDateTime = new Date();
    }

    /**
     * 获取HTTP的请求方法。
     *
     * @return HTTP的请求方法。
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 设置HTTP的请求方法。
     *
     * @param method HTTP的请求方法。
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * @return the endpoint
     */
    public URI getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    /**
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        assertParameterNotNull(parameters, "parameters");

        this.parameters = parameters;
    }

    public void addParameter(String key, String value) {
        assertStringNotEmpty(key, "key");

        this.parameters.put(key, value);
    }

    public void removeParameter(String key) {
        assertStringNotEmpty(key, "key");

        this.parameters.remove(key);
    }

    /**
     * Whether or not the request can be repeatedly sent.
     *
     * @return true or false
     */
    public boolean isRepeatable() {
        return this.getContent() == null || this.getContent().markSupported();
    }
}