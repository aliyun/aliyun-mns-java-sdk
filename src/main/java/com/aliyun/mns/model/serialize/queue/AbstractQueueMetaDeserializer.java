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

package com.aliyun.mns.model.serialize.queue;

import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.util.Date;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.ACTIVE_MESSAGES_TAG;
import static com.aliyun.mns.common.MNSConstants.CREATE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.DELAY_MESSAGES_TAG;
import static com.aliyun.mns.common.MNSConstants.DELAY_SECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.INACTIVE_MESSAGES_TAG;
import static com.aliyun.mns.common.MNSConstants.LAST_MODIFY_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.LOGGING_ENABLED_TAG;
import static com.aliyun.mns.common.MNSConstants.MAX_MESSAGE_SIZE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_RETENTION_PERIOD_TAG;
import static com.aliyun.mns.common.MNSConstants.POLLING_WAIT_SECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.QUEUE_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.QUEUE_URL_TAG;
import static com.aliyun.mns.common.MNSConstants.VISIBILITY_TIMEOUT;

public abstract class AbstractQueueMetaDeserializer<T> extends
    XMLDeserializer<T> {

    public AbstractQueueMetaDeserializer() {
        super();
    }

    protected QueueMeta parseQueueMeta(Element root) {
        QueueMeta meta = new QueueMeta();

        String queueName = safeGetElementContent(root, QUEUE_NAME_TAG, null);
        meta.setQueueName(queueName);

        String delaySeconds = safeGetElementContent(root, DELAY_SECONDS_TAG,
            "0");
        meta.setDelaySeconds(Long.parseLong(delaySeconds));

        String maxMessageSize = safeGetElementContent(root,
            MAX_MESSAGE_SIZE_TAG, "0");
        meta.setMaxMessageSize(Long.parseLong(maxMessageSize));

        String messageRetentionPeriod = safeGetElementContent(root,
            MESSAGE_RETENTION_PERIOD_TAG, "0");
        meta.setMessageRetentionPeriod(Long.parseLong(messageRetentionPeriod));

        String visibiltyTimeout = safeGetElementContent(root,
            VISIBILITY_TIMEOUT, "0");
        meta.setVisibilityTimeout(Long.parseLong(visibiltyTimeout));

        String createTime = safeGetElementContent(root, CREATE_TIME_TAG, "0");
        meta.setCreateTime(new Date(Long.parseLong(createTime) * 1000));

        String lastModifyTime = safeGetElementContent(root, LAST_MODIFY_TIME_TAG,
            "0");
        meta.setLastModifyTime(new Date(Long.parseLong(lastModifyTime) * 1000));

        String waitSeconds = safeGetElementContent(root, POLLING_WAIT_SECONDS_TAG,
            "0");
        meta.setPollingWaitSeconds(Integer.parseInt(waitSeconds));

        String activeMessages = safeGetElementContent(root,
            ACTIVE_MESSAGES_TAG, "0");
        meta.setActiveMessages(Long.parseLong(activeMessages));

        String inactiveMessages = safeGetElementContent(root,
            INACTIVE_MESSAGES_TAG, "0");
        meta.setInactiveMessages(Long.parseLong(inactiveMessages));

        String delayMessages = safeGetElementContent(root, DELAY_MESSAGES_TAG,
            "0");
        meta.setDelayMessages(Long.parseLong(delayMessages));

        String queueURL = safeGetElementContent(root, QUEUE_URL_TAG, null);
        meta.setQueueURL(queueURL);

        String loggingEnabled = safeGetElementContent(root, LOGGING_ENABLED_TAG,
            "false");
        meta.setLoggingEnabled(Boolean.parseBoolean(loggingEnabled));

        return meta;
    }

}