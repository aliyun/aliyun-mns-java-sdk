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

package com.aliyun.mns.model;

public class SubscriptionMeta {
    private String subscriptionName;
    private String endpoint;
    private NotifyStrategy notifyStrategy;
    private String subscriptionURL;
    private String topicName;
    private String topicOwner;
    private String status;
    private Long createTime;
    private Long lastModifyTime;
    private NotifyContentFormat contentFormat;
    private String filterTag;

    public SubscriptionMeta() {
        subscriptionName = null;
        endpoint = null;
        notifyStrategy = null;
        subscriptionURL = null;
        topicName = null;
        topicOwner = null;
        status = null;
        createTime = 0L;
        lastModifyTime = 0L;
        contentFormat = null;
        filterTag = null;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicOwner() {
        return topicOwner;
    }

    public void setTopicOwner(String topicOwner) {
        this.topicOwner = topicOwner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getSubscriptionURL() {
        return subscriptionURL;
    }

    public void setSubscriptionURL(String subscriptionURL) {
        this.subscriptionURL = subscriptionURL;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public NotifyStrategy getNotifyStrategy() {
        return notifyStrategy;
    }

    public void setNotifyStrategy(NotifyStrategy notifyStrategy) {
        this.notifyStrategy = notifyStrategy;
    }

    public NotifyContentFormat getNotifyContentFormat() {
        return this.contentFormat;
    }

    public void setNotifyContentFormat(NotifyContentFormat contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getFilterTag() {
        return filterTag;
    }

    /**
     * 最多16个字符
     *
     * @param filterTag tag
     */
    public void setFilterTag(String filterTag) {
        this.filterTag = filterTag;
    }

    public enum NotifyStrategy {
        BACKOFF_RETRY,
        EXPONENTIAL_DECAY_RETRY
    }

    public enum NotifyContentFormat {
        XML,
        SIMPLIFIED,
        JSON,
        STREAM
    }
}
