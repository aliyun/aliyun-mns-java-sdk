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
import com.aliyun.mns.model.serialize.XMLSerializer;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.mns.common.MNSConstants.ENDPOINT_TAG;
import static com.aliyun.mns.common.MNSConstants.FILTER_TAG_TAG;
import static com.aliyun.mns.common.MNSConstants.NOTIFY_CONTENT_FORMAT_TAG;
import static com.aliyun.mns.common.MNSConstants.NOTIFY_STRATEGY_TAG;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_TAG;

public class SubscriptionSerializer extends XMLSerializer<SubscriptionMeta> {

    @Override
    public String getRootTag() {
        return SUBSCRIPTION_TAG;
    }

    @Override
    public Map<String, Getter<SubscriptionMeta, Object>> buildGetterMap() {
        return new HashMap<String, Getter<SubscriptionMeta, Object>>() {{
            put(NOTIFY_STRATEGY_TAG, SubscriptionMeta::getNotifyStrategy);
            put(ENDPOINT_TAG, SubscriptionMeta::getEndpoint);
            put(NOTIFY_CONTENT_FORMAT_TAG, SubscriptionMeta::getNotifyContentFormat);
            put(FILTER_TAG_TAG, SubscriptionMeta::getFilterTag);
        }};
    }

}
