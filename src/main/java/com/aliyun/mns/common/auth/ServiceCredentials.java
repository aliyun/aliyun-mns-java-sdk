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

package com.aliyun.mns.common.auth;

import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import org.apache.commons.lang3.StringUtils;

import static com.aliyun.mns.common.utils.CodingUtils.assertParameterNotNull;

/**
 * 表示用户访问的授权信息。
 */
public class ServiceCredentials {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private AlibabaCloudCredentialsProvider credentialsProvider;

    /**
     * 构造函数。
     */
    public ServiceCredentials() {
    }

    /**
     * 构造函数。
     *
     * @param accessKeyId     Access Key ID.
     * @param accessKeySecret Access Key Secret.
     * @param securityToken   security temp token. (optional)
     * @throws NullPointerException accessKeyId.accessKeySecret.....
     */
    public ServiceCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
        setAccessKeyId(accessKeyId);
        setAccessKeySecret(accessKeySecret);
        setSecurityToken(securityToken);
    }

    /**
     * 构造函数。
     *
     * @param accessKeyId     Access Key ID。
     * @param accessKeySecret Access Key Secret。
     * @throws NullPointerException accessKeyId或accessKeySecret为空指针。
     */
    public ServiceCredentials(String accessKeyId, String accessKeySecret) {
        this(accessKeyId, accessKeySecret, "");
    }

    /**
     * 构造函数。
     *
     * @param credentialProvider credential provider.
     * @throws NullPointerException accessKeyId或accessKeySecret为空
     */
    public ServiceCredentials(AlibabaCloudCredentialsProvider credentialProvider) {
        setCredentialsProvider(credentialProvider);
    }

    /**
     * 获取访问用户的Access Key ID。
     *
     * @return Access Key ID。
     */
    public String getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * 设置访问用户的Access ID。
     *
     * @param accessKeyId Access Key ID。
     */
    public void setAccessKeyId(String accessKeyId) {
        accessKeyId = StringUtils.trimToNull(accessKeyId);
        assertParameterNotNull(accessKeyId, "accessKeyId");
        this.accessKeyId = accessKeyId;
    }

    /**
     * 获取访问用户的Access Key Secret。
     *
     * @return Access Key Secret。
     */
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    /**
     * 设置访问用户的Access Key Secret。
     *
     * @param accessKeySecret Access Key Secret。
     */
    public void setAccessKeySecret(String accessKeySecret) {
        accessKeySecret = StringUtils.trimToNull(accessKeySecret);
        assertParameterNotNull(accessKeySecret, "accessKeySecret");

        this.accessKeySecret = accessKeySecret;
    }

    /**
     * 获取security token。
     *
     * @return security token
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * 设置访问用户的security token
     *
     * @param securityToken token.
     */
    public void setSecurityToken(String securityToken) {
        this.securityToken = StringUtils.trimToNull(securityToken);
    }

    /**
     * 获取credential provider
     *
     * @return predential provider.
     */
    public AlibabaCloudCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * 设置访问用户的credential provider
     *
     * @param credentialsProvider Provider.
     */
    public void setCredentialsProvider(AlibabaCloudCredentialsProvider credentialsProvider) {
        assertParameterNotNull(credentialsProvider, "credentialsProvider");
        this.credentialsProvider = credentialsProvider;
    }
}
