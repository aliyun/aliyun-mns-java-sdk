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

package com.aliyun.mns.model.serialize.topic;

import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.CREATE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.DEFAULT_NOTIFY_CONTENT_TYPE;
import static com.aliyun.mns.common.MNSConstants.ENDPOINT_TAG;
import static com.aliyun.mns.common.MNSConstants.FILTER_TAG_TAG;
import static com.aliyun.mns.common.MNSConstants.LAST_MODIFY_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.NOTIFY_CONTENT_FORMAT_TAG;
import static com.aliyun.mns.common.MNSConstants.NOTIFY_STRATEGY_TAG;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_STATUS;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_URL_TAG;
import static com.aliyun.mns.common.MNSConstants.TOPIC_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.TOPIC_OWNER_TAG;

public abstract class AbstractSubscriptionDeserializer<T> extends XMLDeserializer<T> {
    private SubscriptionMeta.NotifyStrategy str2Strategy(String str) {
        if (str.trim().equalsIgnoreCase(SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY.toString())) {
            return SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY;
        } else if (str.trim().equalsIgnoreCase(SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY.toString())) {
            return SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY;
        } else {
            return SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY;
        }
    }

    private SubscriptionMeta.NotifyContentFormat str2Format(String str) {
        if (str.trim().equalsIgnoreCase(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED.toString())) {
            return SubscriptionMeta.NotifyContentFormat.SIMPLIFIED;
        } else if (str.trim().equalsIgnoreCase(SubscriptionMeta.NotifyContentFormat.JSON.toString())) {
            return SubscriptionMeta.NotifyContentFormat.JSON;
        } else if (str.trim().equalsIgnoreCase(SubscriptionMeta.NotifyContentFormat.STREAM.toString())) {
            return SubscriptionMeta.NotifyContentFormat.STREAM;
        } else {
            return SubscriptionMeta.NotifyContentFormat.XML;
        }
    }

    protected SubscriptionMeta parseMeta(Element root) {
        SubscriptionMeta meta = new SubscriptionMeta();
        String topicName = safeGetElementContent(root, TOPIC_NAME_TAG, null);
        if (topicName != null) {
            meta.setTopicName(topicName);
        }

        String subscriptionName = safeGetElementContent(root, SUBSCRIPTION_NAME_TAG, null);
        if (subscriptionName != null) {
            meta.setSubscriptionName(subscriptionName);
        }

        String topicOwner = safeGetElementContent(root, TOPIC_OWNER_TAG, null);
        if (topicOwner != null) {
            meta.setTopicOwner(topicOwner);
        }

        String status = safeGetElementContent(root, SUBSCRIPTION_STATUS, null);
        if (status != null) {
            meta.setStatus(status);
        }

        String endpoint = safeGetElementContent(root, ENDPOINT_TAG, null);
        if (endpoint != null) {
            meta.setEndpoint(endpoint);
        }

        String notifyStrategy = safeGetElementContent(root, NOTIFY_STRATEGY_TAG, null);
        if (notifyStrategy != null) {
            meta.setNotifyStrategy(str2Strategy(notifyStrategy));
        }

        String createTime = safeGetElementContent(root, CREATE_TIME_TAG, null);
        if (createTime != null) {
            meta.setCreateTime(Long.parseLong(createTime));
        }

        String lastModifyTime = safeGetElementContent(root, LAST_MODIFY_TIME_TAG, null);
        if (lastModifyTime != null) {
            meta.setLastModifyTime(Long.parseLong(lastModifyTime));
        }

        String notifyContentFormat = safeGetElementContent(root, NOTIFY_CONTENT_FORMAT_TAG, DEFAULT_NOTIFY_CONTENT_TYPE);
        meta.setNotifyContentFormat(str2Format(notifyContentFormat));

        String filterTag = safeGetElementContent(root, FILTER_TAG_TAG, null);
        if (filterTag != null) {
            meta.setFilterTag(filterTag);
        }

        String subscriptionURL = safeGetElementContent(root, SUBSCRIPTION_URL_TAG, null);
        if (subscriptionURL != null) {
            meta.setSubscriptionURL(subscriptionURL);
        }
        return meta;
    }

}
