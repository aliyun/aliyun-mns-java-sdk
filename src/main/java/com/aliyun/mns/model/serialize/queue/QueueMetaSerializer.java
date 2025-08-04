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
import com.aliyun.mns.model.serialize.XMLSerializer;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.mns.common.MNSConstants.DELAY_SECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.LOGGING_ENABLED_TAG;
import static com.aliyun.mns.common.MNSConstants.MAX_MESSAGE_SIZE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_RETENTION_PERIOD_TAG;
import static com.aliyun.mns.common.MNSConstants.POLLING_WAITSECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.QUEUE_TAG;
import static com.aliyun.mns.common.MNSConstants.VISIBILITY_TIMEOUT;

public class QueueMetaSerializer extends XMLSerializer<QueueMeta> {

    @Override
    public String getRootTag() {
        return QUEUE_TAG;
    }

    @Override
    public Map<String, Getter<QueueMeta, Object>> buildGetterMap() {
        return new HashMap<String, Getter<QueueMeta, Object>>() {{
            put(DELAY_SECONDS_TAG, QueueMeta::getDelaySeconds);
            put(VISIBILITY_TIMEOUT, QueueMeta::getVisibilityTimeout);
            put(MAX_MESSAGE_SIZE_TAG, QueueMeta::getMaxMessageSize);
            put(MESSAGE_RETENTION_PERIOD_TAG, QueueMeta::getMessageRetentionPeriod);
            put(POLLING_WAITSECONDS_TAG, QueueMeta::getPollingWaitSeconds);
            put(LOGGING_ENABLED_TAG, QueueMeta::isLoggingEnabled);
        }};
    }

}
