package com.aliyun.mns.sample.scenarios.largeMessage.service.bean;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.oss.OSS;

public class MNSExtendedConfiguration {

    /**
     * 存储的 oss 客户端
     */
    private OSS ossClient;

    /**
     * 存储的 oss bucketName
     */
    private String ossBucketName;

    /**
     * 限额大小，单位：B
     * 小于此值，走 mns 原生服务，大于此值，走 OSS 大文件逻辑
     */
    private Long payloadSizeThreshold;

    /**
     * MNS queue 对象
     */
    private CloudQueue MNSQueue;

    /**
     * MNS topic 对象
     */
    private CloudTopic MNSTopic;

    public OSS getOssClient() {
        return ossClient;
    }

    public MNSExtendedConfiguration setOssClient(OSS ossClient) {
        if (ossClient != null) {
            this.ossClient = ossClient;
        }
        return this;
    }

    public String getOssBucketName() {
        return ossBucketName;
    }

    public MNSExtendedConfiguration setOssBucketName(String ossBucketName) {
        if (ossBucketName != null) {
            this.ossBucketName = ossBucketName;
        }
        return this;
    }

    public Long getPayloadSizeThreshold() {
        return payloadSizeThreshold;
    }

    public MNSExtendedConfiguration setPayloadSizeThreshold(Long payloadSizeThreshold) {
        if (payloadSizeThreshold != null) {
            this.payloadSizeThreshold = payloadSizeThreshold;
        }
        return this;
    }

    public CloudQueue getMNSQueue() {
        return MNSQueue;
    }

    public MNSExtendedConfiguration setMNSQueue(CloudQueue MNSQueue) {
        if (MNSQueue != null) {
            this.MNSQueue = MNSQueue;
        }
        return this;
    }

    public CloudTopic getMNSTopic() {
        return MNSTopic;
    }

    public MNSExtendedConfiguration setMNSTopic(CloudTopic MNSTopic) {
        if (MNSTopic != null) {
            this.MNSTopic = MNSTopic;
        }
        return this;
    }
}
