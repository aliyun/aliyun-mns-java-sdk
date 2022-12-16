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

import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.CREATE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.LASTMODIFYTIME_TAG;
import static com.aliyun.mns.common.MNSConstants.LOGGING_ENABLED_TAG;
import static com.aliyun.mns.common.MNSConstants.MAX_MESSAGE_SIZE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_COUNT_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_RETENTION_PERIOD_TAG;
import static com.aliyun.mns.common.MNSConstants.TOPIC_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.TOPIC_URL_TAG;

public abstract class AbstractTopicMetaDeserializer<T> extends XMLDeserializer<T> {
    public AbstractTopicMetaDeserializer() {
        super();
    }

    protected TopicMeta parseMeta(Element root) {
        TopicMeta meta = new TopicMeta();
        String topicName = safeGetElementContent(root, TOPIC_NAME_TAG, null);
        meta.setTopicName(topicName);

        String messageCount = safeGetElementContent(root,
            MESSAGE_COUNT_TAG, "0");
        meta.setMessageCount(Long.parseLong(messageCount));

        String createTime = safeGetElementContent(root, CREATE_TIME_TAG, "0");
        meta.setCreateTime(Long.parseLong(createTime));

        String lastModifyTime = safeGetElementContent(root, LASTMODIFYTIME_TAG, "0");
        meta.setLastModifyTime(Long.parseLong(lastModifyTime));

        String maxMessageSize = safeGetElementContent(root,
            MAX_MESSAGE_SIZE_TAG, "0");
        meta.setMaxMessageSize(Long.parseLong(maxMessageSize));

        String messageRetentionPeriod = safeGetElementContent(root,
            MESSAGE_RETENTION_PERIOD_TAG, "0");
        meta.setMessageRetentionPeriod(Long.parseLong(messageRetentionPeriod));

        String topicURL = safeGetElementContent(root, TOPIC_URL_TAG, null);
        meta.setTopicURL(topicURL);

        String loggingEnabled = safeGetElementContent(root,
            LOGGING_ENABLED_TAG, "false");
        meta.setLoggingEnabled(Boolean.parseBoolean(loggingEnabled));

        return meta;
    }
}
