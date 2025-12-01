package com.aliyun.mns.client;

import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;

public class MNSClientBuilder {

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private AlibabaCloudCredentialsProvider credentialsProvider;
    private String accountEndpoint;
    private ClientConfiguration clientConfiguration;
    private String region;

    public static MNSClientBuilder create() {
        return new MNSClientBuilder();
    }

    public MNSClientBuilder accessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public MNSClientBuilder accessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public MNSClientBuilder credentialsProvider(AlibabaCloudCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public MNSClientBuilder securityToken(String securityToken) {
        this.securityToken = securityToken;
        return this;
    }

    public MNSClientBuilder accountEndpoint(String accountEndpoint) {
        this.accountEndpoint = accountEndpoint;
        return this;
    }

    public MNSClientBuilder clientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        return this;
    }

    public MNSClientBuilder region(String regionId) {
        this.region = regionId;
        return this;
    }

    public MNSClient build() {
        CloudAccount cloudAccount = new CloudAccount(accessKeyId, accessKeySecret, accountEndpoint, securityToken,
            credentialsProvider, clientConfiguration, region);
        return cloudAccount.getMNSClient();
    }
}
