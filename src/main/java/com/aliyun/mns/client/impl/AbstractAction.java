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

package com.aliyun.mns.client.impl;

import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.client.AsyncResult;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.auth.ServiceSignature;
import com.aliyun.mns.common.comm.ExecutionContext;
import com.aliyun.mns.common.http.ExceptionResultParser;
import com.aliyun.mns.common.http.HttpCallback;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.common.utils.DateUtil;
import com.aliyun.mns.model.AbstractRequest;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Future;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction<T extends AbstractRequest, V> implements
    Action<T, V> {

    public static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);
    protected String actionName = "";
    private ServiceClient client;
    private ServiceCredentials credentials;
    private HttpMethod method;
    private URI endpoint;
    private String userRequestId = null;

    public AbstractAction(HttpMethod method, String actionName,
        ServiceClient client, ServiceCredentials credentials, URI endpoint) {
        this.method = method;
        this.actionName = actionName;
        this.client = client;
        this.endpoint = endpoint;
        this.credentials = credentials;

        if (this.client.getClientConfiguration().isGenerateRequestId()) {
            userRequestId = UUID.randomUUID().toString();
        }
    }

    private static TreeMap<String, String> sortHeader(
        Map<String, String> headers) {
        TreeMap<String, String> tmpHeaders = new TreeMap<String, String>();
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            if (key.toLowerCase().startsWith(MNSConstants.X_HEADER_MNS_PREFIX)) {
                tmpHeaders.put(key.toLowerCase(), headers.get(key));
            } else {
                tmpHeaders.put(key, headers.get(key));
            }
        }
        return tmpHeaders;
    }

    private static String safeGetHeader(String key, Map<String, String> headers) {
        if (headers == null) {
            return "";
        }
        String value = headers.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getActionName() {
        return actionName;
    }

    public ServiceClient getClient() {
        return client;
    }

    public ServiceCredentials getCredentials() {
        return credentials;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getEndpoint() {
        return this.endpoint;
    }

    public AsyncResult<V> execute(T reqObject, AsyncCallback<V> asyncHandler)
        throws ClientException, ServiceException {
        return this.executeWithCustomHeaders(reqObject, asyncHandler, null);
    }

    public AsyncResult<V> executeWithCustomHeaders(T reqObject, AsyncCallback<V> asyncHandler,
        Map<String, String> customHeaders)
        throws ClientException, ServiceException {
        RequestMessage request = buildRequestMessage(reqObject);
        request.setMethod(this.getMethod());
        this.addRequiredHeader(request);
        this.addCustomHeader(request, customHeaders);
        this.addSignatureHeader(request);

        HttpCallback<V> callback = new HttpCallback<V>(
            this.buildResultParser(), this.buildExceptionParser(), asyncHandler, userRequestId);
        AsyncResult<V> asyncResult = callback.getAsyncResult();
        asyncResult.setTimewait(this.client.getClientConfiguration().getSocketTimeout());
        Future<HttpResponse> future = client.sendRequest(request, new ExecutionContext(), callback);
        asyncResult.setFuture(future);
        return asyncResult;
    }

    @Override
    public V execute(T reqObject) throws ClientException, ServiceException {
        return this.executeWithCustomHeaders(reqObject, null);
    }

    public V executeWithCustomHeaders(T reqObject, Map<String, String> customHeaders)
        throws ClientException, ServiceException {
        AsyncResult<V> result = executeWithCustomHeaders(reqObject, null, customHeaders);
        V value = result.getResult();
        if (result.isSuccess()) {
            return value;
        }

        if (result.getException() instanceof ClientException) {
            throw (ClientException) result.getException();
        } else if (result.getException() instanceof ServiceException) {
            throw (ServiceException) result.getException();
        } else {
            ClientException ce = new ClientException(result.getException().toString(),
                userRequestId, result.getException());
            ce.setStackTrace(result.getException().getStackTrace());
            throw ce;
        }
    }

    private void addCustomHeader(RequestMessage request, Map<String, String> customHeaders) {
        if (customHeaders == null || customHeaders.size() == 0) {
            return;
        }
        for (String key : customHeaders.keySet()) {
            request.getHeaders().put(key, customHeaders.get(key));
        }
    }

    protected void addRequiredHeader(RequestMessage request) {
        request.getHeaders().put(MNSConstants.X_HEADER_MNS_API_VERSION,
            MNSConstants.X_HEADER_MNS_API_VERSION_VALUE);

        if (request.getHeaders().get(MNSConstants.DATE) == null) {
            request.getHeaders().put(MNSConstants.DATE,
                DateUtil.formatRfc822Date(new Date()));
        }

        if (request.getHeaders().get(MNSConstants.CONTENT_TYPE) == null) {
            request.getHeaders().put(MNSConstants.CONTENT_TYPE,
                MNSConstants.DEFAULT_CONTENT_TYPE);
        }

        if (userRequestId != null) {
            request.getHeaders().put(MNSConstants.MNS_USER_REQUEST_ID,
                userRequestId);
        }
    }

    protected void addSignatureHeader(RequestMessage request)
        throws ClientException {
        if (credentials != null) {
            // Add signature
            if ((credentials.getAccessKeyId() == null || credentials.getAccessKeySecret() == null)
                && credentials.getCredentialsProvider() == null) {
                return;
            }
            String accessKeyId;
            if (credentials.getAccessKeyId() != null && credentials.getAccessKeySecret() != null) {
                accessKeyId = credentials.getAccessKeyId();
            } else if (credentials.getCredentialsProvider() != null) {
                accessKeyId = credentials.getAccessKeyIdByProvider();
            } else {
                accessKeyId = null;
            }
            request.addHeader(MNSConstants.AUTHORIZATION,
                "MNS " + accessKeyId + ":"
                    + getSignature(request)
            );

            // add security_token if security token is not empty.
            String securityToken;
            if (credentials.getAccessKeyId() != null && credentials.getAccessKeySecret() != null) {
                securityToken = credentials.getSecurityToken();
            } else if (credentials.getCredentialsProvider() != null) {
                securityToken = credentials.getSecurityTokenByProvider();
            } else {
                securityToken = null;
            }
            if (securityToken != null && !"".equals(securityToken)) {
                request.addHeader(MNSConstants.SECURITY_TOKEN, securityToken);
            }
        }
    }

    private String getRelativeResourcePath(String subPath) {
        String rootPath = endpoint.getPath();
        if (subPath != null && !"".equals(subPath.trim())) {
            if (subPath.startsWith("/")) {
                subPath = subPath.substring(1);
            }
            if (!rootPath.endsWith("/")) {
                return rootPath + "/" + subPath;
            }
            return rootPath + subPath;
        }
        return rootPath;
    }

    private String getSignature(RequestMessage request) throws ClientException {
        Map<String, String> headers = request.getHeaders();

        StringBuffer canonicalizedMNSHeaders = new StringBuffer();
        StringBuffer stringToSign = new StringBuffer();
        String contentMd5 = safeGetHeader(MNSConstants.CONTENT_MD5, headers);
        String contentType = safeGetHeader(MNSConstants.CONTENT_TYPE, headers);
        String date = safeGetHeader(MNSConstants.DATE, headers);
        String canonicalizedResource = getRelativeResourcePath(request
            .getResourcePath());

        TreeMap<String, String> tmpHeaders = sortHeader(request.getHeaders());
        if (tmpHeaders.size() > 0) {
            Set<String> keySet = tmpHeaders.keySet();
            for (String key : keySet) {
                if (key.toLowerCase().startsWith(
                    MNSConstants.X_HEADER_MNS_PREFIX)) {
                    canonicalizedMNSHeaders.append(key).append(":")
                        .append(tmpHeaders.get(key)).append("\n");
                }
            }
        }
        stringToSign.append(method).append("\n").append(contentMd5)
            .append("\n").append(contentType).append("\n").append(date)
            .append("\n").append(canonicalizedMNSHeaders)
            .append(canonicalizedResource);
        String signature;

        try {
            if (credentials.getAccessKeyId() != null && credentials.getAccessKeySecret() != null) {
                signature = ServiceSignature.create().computeSignature(
                    credentials.getAccessKeySecret(), stringToSign.toString());
            } else if (credentials.getCredentialsProvider() != null) {
                signature = ServiceSignature.create().computeSignature(
                    credentials.getAccessKeySecretByProvider(), stringToSign.toString());
            } else {
                signature = null;
            }
        } catch (Exception e) {
            throw new ClientException("Signature fail", userRequestId, e);
        }

        return signature;
    }

    protected RequestMessage buildRequestMessage(T reqObject)
        throws ClientException {
        RequestMessage request = buildRequest(reqObject);
        String requestPath = request.getResourcePath();
        if (requestPath != null && (requestPath.startsWith("http://") || requestPath.startsWith("https://"))) {
            if (!requestPath.startsWith(endpoint.toString())) {
                throw new IllegalArgumentException("Endpoint["
                    + endpoint.toString() + "]和访问地址[" + requestPath
                    + "]不匹配.");
            } else {
                requestPath = requestPath.substring(endpoint.toString().length());
                if (requestPath.startsWith("/")) {
                    requestPath = requestPath.substring(1);
                }
                request.setResourcePath(requestPath);
            }
        }
        request.setEndpoint(endpoint);

        return request;
    }

    public String getUserRequestId() {
        return userRequestId;
    }

    protected ResultParser<V> buildResultParser() {
        return null;
    }

    protected ResultParser<Exception> buildExceptionParser() {
        return new ExceptionResultParser(userRequestId);
    }

    protected abstract RequestMessage buildRequest(T reqObject)
        throws ClientException;
}
