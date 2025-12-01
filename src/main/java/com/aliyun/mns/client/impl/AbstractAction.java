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
import com.aliyun.mns.common.auth.MNSV2Signer;
import com.aliyun.mns.common.auth.MNSV4Signer;
import com.aliyun.mns.common.auth.RequestSigner;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.comm.ExecutionContext;
import com.aliyun.mns.common.http.ExceptionResultParser;
import com.aliyun.mns.common.http.HttpCallback;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.common.utils.AlibabaCloudCredentialsUtil;
import com.aliyun.mns.common.utils.DateUtil;
import com.aliyun.mns.common.utils.IdptEnvUtil;
import com.aliyun.mns.model.AbstractRequest;
import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAction<T extends AbstractRequest, V> implements
    Action<T, V> {

    public static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);
    protected String actionName = "";
    private ServiceClient client;
    private ServiceCredentials credentials;
    private RequestSigner requestSigner;
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
        this.requestSigner = createRequestSigner();

        if (this.client.getClientConfiguration().isGenerateRequestId()) {
            userRequestId = UUID.randomUUID().toString();
        }
    }

    private RequestSigner createRequestSigner() {
        SignVersion signVersion = this.client.getClientConfiguration().getSignatureVersion();
        if (signVersion == SignVersion.V2 && IdptEnvUtil.isIdptEnv()) {
            throw new ClientException(userRequestId, "Unsupported Signature Version: " + signVersion);
        }
        if (signVersion == null) {
            signVersion = IdptEnvUtil.isIdptEnv() ? SignVersion.V4 : SignVersion.V2;
        }
        if (signVersion == SignVersion.V2) {
            return new MNSV2Signer();
        } else if (signVersion == SignVersion.V4) {
            return new MNSV4Signer();
        } else {
            throw new ClientException(userRequestId, "Unsupported Signature Version: " + signVersion);
        }
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public ServiceClient getClient() {
        return client;
    }

    @Override
    public ServiceCredentials getCredentials() {
        return credentials;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    public URI getEndpoint() {
        return this.endpoint;
    }

    @Override
    public AsyncResult<V> execute(T reqObject, AsyncCallback<V> asyncHandler)
        throws ClientException, ServiceException {
        return this.executeWithCustomHeaders(reqObject, asyncHandler, null);
    }

    public AsyncResult<V> executeWithCustomHeaders(T reqObject, AsyncCallback<V> asyncHandler,
                                                   Map<String, String> customHeaders)
        throws ClientException, ServiceException {
        RequestMessage request = buildRequestMessage(reqObject);
        this.addRequiredHeader(request);
        this.addCustomHeader(request, customHeaders);
        this.addSignatureHeader(request);

        HttpCallback<V> callback = new HttpCallback<V>(
            this.buildResultParser(), this.buildExceptionParser(), asyncHandler, userRequestId);
        long timewaitMillis = this.client.getClientConfiguration().getSocketTimeout();
        return client.asyncSendRequest(request, new ExecutionContext(), callback, timewaitMillis);
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
            throw (ClientException)result.getException();
        } else if (result.getException() instanceof ServiceException) {
            throw (ServiceException)result.getException();
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

    private void addRequiredHeader(RequestMessage request) {
        request.getHeaders().put(MNSConstants.X_HEADER_MNS_API_VERSION,
            MNSConstants.X_HEADER_MNS_API_VERSION_VALUE);

        if (request.getHeaders().get(MNSConstants.DATE) == null) {
            request.getHeaders().put(MNSConstants.DATE,
                DateUtil.formatRfc822Date(request.getRequestDateTime()));
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

    private void addSignatureHeader(RequestMessage request)
        throws ClientException {
        if (credentials == null) {
            return;
        }
        AlibabaCloudCredentialsProvider provider = credentials.getCredentialsProvider();

        if ((credentials.getAccessKeyId() == null || credentials.getAccessKeySecret() == null) && provider == null) {
            return;
        }
        // ak/sk 模式 和 credentials 模式
        boolean akSkMode = (credentials.getAccessKeyId() != null && credentials.getAccessKeySecret() != null);
        boolean alibabaCredentialsMode = (provider != null);

        // init ak/sk/token
        String accessKeyId = null;
        String accessKeySecret = null;
        String securityToken = null;

        if (akSkMode) {
            accessKeyId = credentials.getAccessKeyId();
            accessKeySecret = credentials.getAccessKeySecret();
            securityToken = credentials.getSecurityToken();
        } else if (alibabaCredentialsMode) {
            AlibabaCloudCredentials alibabaCloudCredentials = getAlibabaCloudCredentials(provider);
            if (alibabaCloudCredentials != null) {
                accessKeyId = alibabaCloudCredentials.getAccessKeyId();
                accessKeySecret = alibabaCloudCredentials.getAccessKeySecret();
                securityToken = AlibabaCloudCredentialsUtil.getSecurityToken(alibabaCloudCredentials);
            }
        }

        String region = client.getRegion();
        request.addHeader(MNSConstants.AUTHORIZATION,
            requestSigner.getAuthorization(accessKeyId, accessKeySecret, request, region));

        // add security_token if security token is not empty.
        if (StringUtils.isNotBlank(securityToken)) {
            request.addHeader(MNSConstants.SECURITY_TOKEN, securityToken);
        }
    }

    private AlibabaCloudCredentials getAlibabaCloudCredentials(AlibabaCloudCredentialsProvider provider) {
        if (provider == null) {
            return null;
        }

        try {
            return provider.getCredentials();
        } catch (Exception e) {
            logger.error("get credentials failed, errorMsg:" + e.getMessage(), e);
            throw new ClientException(e);
        }
    }

    private RequestMessage buildRequestMessage(T reqObject)
        throws ClientException {
        RequestMessage request = buildRequest(reqObject);
        String requestPath = request.getResourcePath();
        if (requestPath != null && (requestPath.startsWith("http://") || requestPath.startsWith("https://"))) {
            if (!requestPath.startsWith(endpoint.toString())) {
                throw new IllegalArgumentException("The endpoint ["
                    + endpoint + "] does not match the request path [" + requestPath + "].");
            } else {
                requestPath = requestPath.substring(endpoint.toString().length());
                if (requestPath.startsWith("/")) {
                    requestPath = requestPath.substring(1);
                }
                request.setResourcePath(requestPath);
            }
        }
        request.setEndpoint(endpoint);
        request.setMethod(method);

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
