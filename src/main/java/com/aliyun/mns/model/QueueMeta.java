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

import java.util.Date;

public class QueueMeta {
    protected String queueName = null;
    protected Long delaySeconds = null;
    protected Long messageRetentionPeriod = null;
    protected Long maxMessageSize = null;
    protected Long visibilityTimeout = null;

    protected Date createTime = null;
    protected Date lastModifyTime = null;
    protected Integer pollingWaitSeconds = null;

    @Deprecated
    protected Long activeMessages = null;
    @Deprecated
    protected Long inactiveMessages = null;
    @Deprecated
    protected Long delayMessages = null;
    protected String queueURL = null;
    protected boolean loggingEnabled;

    /**
     * 队列是否开通了Logging功能
     *
     * @return boolean
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * 设置开通队列的Logging功能
     *
     * @param loggingEnabled enable or not
     */
    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    /**
     * 获取队列的名字
     *
     * @return queue name
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * 设置队列的名字
     *
     * @param queueName queue name
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * 获取队列的延时消息的延时，单位是秒
     *
     * @return delay seconds
     */
    public Long getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * 设置队列的延时消息的延时，单位是秒
     *
     * @param delaySeconds delay seconds
     */
    public void setDelaySeconds(Long delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    /**
     * 获取队列消息的最长存活时间，单位是秒
     *
     * @return message retention period
     */
    public Long getMessageRetentionPeriod() {
        return messageRetentionPeriod;
    }

    /**
     * 设置队列消息的最长存活时间，单位是秒
     *
     * @param messageRetentionPeriod period
     */
    public void setMessageRetentionPeriod(Long messageRetentionPeriod) {
        this.messageRetentionPeriod = messageRetentionPeriod;
    }

    /**
     * 获取队列消息的最大长度，单位是byte
     *
     * @return max message size
     */
    public Long getMaxMessageSize() {
        return maxMessageSize;
    }

    /**
     * 设置队列消息的最大长度，单位是byte
     *
     * @param maxMessageSize max message size
     */
    public void setMaxMessageSize(Long maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    /**
     * 获取队列消息的长轮询等待时间，单位是秒
     *
     * @return seconds
     */
    public Integer getPollingWaitSeconds() {
        return pollingWaitSeconds;
    }

    /**
     * 设置队列消息的长轮询等待时间，单位是秒
     *
     * @param pollingWaitseconds polling wait seconds
     */
    public void setPollingWaitSeconds(Integer pollingWaitseconds) {
        this.pollingWaitSeconds = pollingWaitseconds;
    }

    /**
     * 获取队列消息的不可见时间，单位是秒
     *
     * @return visibility timeout
     */
    public Long getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * 设置队列消息的不可见时间，单位是秒
     *
     * @param visibilityTimeout visibility timeout
     */
    public void setVisibilityTimeout(Long visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    /**
     * 获取队列的创建时间
     *
     * @return date
     */
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取队列的最后修改时间
     *
     * @return date
     */
    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    /**
     * 获取队列中活跃消息数
     *
     * @return message num
     */
    @Deprecated
    public Long getActiveMessages() {
        return activeMessages;
    }

    @Deprecated
    public void setActiveMessages(Long activeMessages) {
        this.activeMessages = activeMessages;
    }

    /**
     * 获取队列中不活跃消息数
     *
     * @return message num
     */
    @Deprecated
    public Long getInactiveMessages() {
        return inactiveMessages;
    }

    @Deprecated
    public void setInactiveMessages(Long inactiveMessages) {
        this.inactiveMessages = inactiveMessages;
    }

    /**
     * 获取队列中延时消息数
     *
     * @return message num
     */
    @Deprecated
    public Long getDelayMessages() {
        return delayMessages;
    }

    @Deprecated
    public void setDelayMessages(Long delayMessages) {
        this.delayMessages = delayMessages;
    }

    /**
     * 获取队列的URL
     *
     * @return queue url
     */
    public String getQueueURL() {
        return queueURL;
    }

    /**
     * 设置队列的URL
     *
     * @param queueURL queue url
     */
    public void setQueueURL(String queueURL) {
        this.queueURL = queueURL;
    }

}
