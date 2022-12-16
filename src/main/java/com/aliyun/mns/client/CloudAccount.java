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

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.HttpCallback;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.http.ServiceClientFactory;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CloudAccount {
    private static Logger log = LoggerFactory.getLogger(CloudAccount.class);

    private String accessId;
    private String accessKey;
    private String securityToken;
    private String accountEndpoint;
    private AlibabaCloudCredentialsProvider credentialsProvider;

    // 访问MNS服务的client
    private ServiceClient serviceClient = null;

    // 用户身份信息。
    private ServiceCredentials credentials = new ServiceCredentials();
    private ClientConfiguration config;

    private MNSClient mnsClient;

    /**
     * 设置异步callback需要的ExecutorService
     *
     * @param executor 调用异步接口时, 执行用户callback的ExecutorService
     */
    public static void setCallbackExecutor(ExecutorService executor) {
        HttpCallback.setCallbackExecutor(executor);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint) {
        this(accessId, accessKey, accountEndpoint, "", null, null);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint, String securityToken) {
        this(accessId, accessKey, accountEndpoint, securityToken, null, null);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint, ClientConfiguration config) {
        this(accessId, accessKey, accountEndpoint, "", null, config);
    }

    public CloudAccount(String accountEndpoint, AlibabaCloudCredentialsProvider provider) {
        this(null, null, accountEndpoint, null, provider, null);
    }

    public CloudAccount(String accountEndpoint, AlibabaCloudCredentialsProvider provider, ClientConfiguration config) {
        this(null, null, accountEndpoint, null, provider, config);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint, AlibabaCloudCredentialsProvider provider) {
        this(accessId, accessKey, accountEndpoint, "", provider, null);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint, String securityToken,
        AlibabaCloudCredentialsProvider provider) {
        this(accessId, accessKey, accountEndpoint, securityToken, provider, null);
    }

    public CloudAccount(String accessId, String accessKey,
        String accountEndpoint, String securityToken,
        AlibabaCloudCredentialsProvider provider, ClientConfiguration config) {
        this.accessId = accessId;
        this.accessKey = accessKey;
        this.accountEndpoint = Utils.getHttpURI(accountEndpoint).toString();
        this.securityToken = securityToken;
        this.credentialsProvider = provider;
        this.config = config;

        init();
    }

    public MNSClient getMNSClient() throws ServiceException, ClientException {
        if (mnsClient == null) {
            synchronized (this) {
                if (mnsClient == null) {
                    String accountEndpoint = getAccountEndpoint();
                    try {
                        serviceClient = ServiceClientFactory.createServiceClient(config);
                        mnsClient = new DefaultMNSClient(credentials, serviceClient,
                            accountEndpoint);
                    } catch (Exception e) {
                        if (serviceClient != null) {
                            ServiceClientFactory.closeServiceClient(serviceClient);
                            serviceClient = null;
                        }
                        throw new ClientException(e);
                    }
                }
            }
        }
        return mnsClient;
    }

    private void init() {
        if (this.accessId != null && this.accessKey != null) {
            this.credentials = new ServiceCredentials(accessId, accessKey, securityToken);
        } else {
            this.credentials = new ServiceCredentials(credentialsProvider);
        }
        if (config == null) {
            config = new ClientConfiguration();
        }

        if (log.isDebugEnabled()) {
            log.debug("initiated CloudAccount, accessId=" + accessId + ",accessKey="
                + accessKey + ", endpoint=" + accountEndpoint + " securityToken=" + securityToken);
        }
    }

    /**
     * @return account Endpoint
     */
    public String getAccountEndpoint() {
        return accountEndpoint;
    }

    public void setAccountEndpoint(String endpoint) {
        this.accountEndpoint = Utils.getHttpURI(endpoint).toString();
    }
}
