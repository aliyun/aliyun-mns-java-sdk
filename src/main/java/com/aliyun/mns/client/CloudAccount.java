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
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.HttpCallback;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.http.ServiceClientFactory;
import com.aliyun.mns.common.utils.CodingUtils;
import com.aliyun.mns.common.utils.IdptEnvUtil;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public final class CloudAccount {
    private static Logger log = LoggerFactory.getLogger(CloudAccount.class);

    private final String accessKeyId;
    private final String accessKeySecret;
    private final String securityToken;
    private final String accountEndpoint;
    private final AlibabaCloudCredentialsProvider credentialsProvider;

    // 访问MNS服务的client
    private ServiceClient serviceClient = null;

    // 用户身份信息。
    private ServiceCredentials credentials = new ServiceCredentials();
    private ClientConfiguration config;
    private final String region;

    private MNSClient mnsClient;

    /**
     * 设置异步callback需要的ExecutorService
     *
     * @param executor 调用异步接口时, 执行用户callback的ExecutorService
     */
    public static void setCallbackExecutor(ExecutorService executor) {
        HttpCallback.setCallbackExecutor(executor);
    }

    /**
     * 推荐使用 {@link MNSClientBuilder} 来创建CloudAccount对象。
     *
     * @param accessKeyId     Access Key ID
     * @param accessKeySecret Access Key Secret
     * @param accountEndpoint Account endpoint
     * @param securityToken   Security token (optional)
     * @param provider        Alibaba Cloud credentials provider
     * @param config          Client configuration
     * @param region          Region
     */
    public CloudAccount(String accessKeyId, String accessKeySecret,
                        String accountEndpoint, String securityToken,
                        AlibabaCloudCredentialsProvider provider, ClientConfiguration config,
                        String region) {
        this.accessKeyId = StringUtils.trimToNull(accessKeyId);
        this.accessKeySecret = StringUtils.trimToNull(accessKeySecret);
        this.accountEndpoint = Utils.getHttpURI(accountEndpoint).toString();
        this.securityToken = StringUtils.trimToNull(securityToken);
        this.credentialsProvider = provider;
        this.config = config;
        this.region = region;

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
                            accountEndpoint, region);
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
        if (this.accessKeyId != null && this.accessKeySecret != null) {
            this.credentials = new ServiceCredentials(accessKeyId, accessKeySecret, securityToken);
        } else if (credentialsProvider != null) {
            this.credentials = new ServiceCredentials(credentialsProvider);
        } else {
            // 基于 env ak/sk 兜底
            this.credentials = getCredentialsFromAkSkEnv();
        }

        if (config == null) {
            config = new ClientConfiguration();
        }

        CodingUtils.assertParameterNotNull(this.accountEndpoint, "accountEndpoint");
        CodingUtils.assertParameterNotNull(this.region, "region");

        if (log.isDebugEnabled()) {
            log.debug("initiated CloudAccount, accessKeyId=" + accessKeyId + ",accessKeySecret="
                + accessKeySecret + ", endpoint=" + accountEndpoint + " securityToken=" + securityToken);
        }
    }

    private ServiceCredentials getCredentialsFromAkSkEnv() {
        String akEnvName = IdptEnvUtil.isIdptEnv() ? MNSConstants.IDPT_AK_ENV_KEY : MNSConstants.ALIYUN_AK_ENV_KEY;
        String skEnvName = IdptEnvUtil.isIdptEnv() ? MNSConstants.IDPT_SK_ENV_KEY : MNSConstants.ALIYUN_SK_ENV_KEY;
        return new ServiceCredentials(System.getenv(akEnvName), System.getenv(skEnvName), securityToken);
    }

    /**
     * @return account Endpoint
     */
    public String getAccountEndpoint() {
        return accountEndpoint;
    }

}
