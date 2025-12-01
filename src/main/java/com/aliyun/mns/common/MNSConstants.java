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

package com.aliyun.mns.common;

import com.aliyun.mns.common.utils.HttpHeaders;

public interface MNSConstants extends HttpHeaders {
    String LOCATION = "Location";
    String LOCATION_MESSAGES = "messages";

    String X_HEADER_MNS_API_VERSION = "x-mns-version";
    String X_HEADER_MNS_API_VERSION_VALUE = "2015-06-06";

    String X_HEADER_MNS_PREFIX = "x-mns-";
    String X_HEADER_MNS_QUEUE_PREFIX = "x-mns-prefix";
    String X_HEADER_MNS_MARKER = "x-mns-marker";
    String X_HEADER_MNS_RET_NUMBERS = "x-mns-ret-number";
    String X_HEADER_MNS_WITH_META = "x-mns-with-meta";
    String X_HEADER_MNS_REQUEST_ID = "x-mns-request-id";

    String DEFAULT_CHARSET = "UTF-8";
    String DEFAULT_CONTENT_TYPE = "text/xml;charset=UTF-8";

    String DEFAULT_XML_NAMESPACE = "http://mns.aliyuncs.com/doc/v1";

    String QUEUE_PREFIX = "queues/";
    String TOPIC_PREFIX = "topics/";

    String URI_OPEN_SERVICE = "commonbuy/openservice";

    String ACCOUNT_TAG = "Account";
    String QUEUE_TAG = "Queue";
    String TOPIC_TAG = "Topic";
    String QUEUE_NAME_TAG = "QueueName";
    String TOPIC_NAME_TAG = "TopicName";
    String SUBSCRIPTION_TAG = "Subscription";
    String DELAY_SECONDS_TAG = "DelaySeconds";
    String MAX_MESSAGE_SIZE_TAG = "MaximumMessageSize";
    String MESSAGE_RETENTION_PERIOD_TAG = "MessageRetentionPeriod";
    String VISIBILITY_TIMEOUT = "VisibilityTimeout";
    String ACTIVE_MESSAGES_TAG = "ActiveMessages";
    String INACTIVE_MESSAGES_TAG = "InactiveMessages";
    String DELAY_MESSAGES_TAG = "DelayMessages";
    String LAST_MODIFY_TIME_TAG = "LastModifyTime";
    String CREATE_TIME_TAG = "CreateTime";
    String POLLING_WAIT_SECONDS_TAG = "PollingWaitSeconds";
    String MESSAGE_COUNT_TAG = "MessageCount";
    String LOGGING_BUCKET_TAG = "LoggingBucket";
    String TRACE_ENABLED_TAG = "TraceEnabled";
    String LOGGING_ENABLED_TAG = "LoggingEnabled";
    String USER_PROPERTIES_TAG = "UserProperties";
    String MESSAGE_PROPERTY_TAG = "PropertyValue";
    String SYSTEM_PROPERTIES_TAG = "SystemProperties";
    String MESSAGE_SYSTEM_PROPERTY_TAG = "SystemPropertyValue";
    String PROPERTY_VALUE_TAG = "Value";
    String PROPERTY_NAME_TAG = "Name";
    String PROPERTY_TYPE_TAG = "Type";

    String QUEUE_URL_TAG = "QueueURL";
    String NEXT_MARKER_TAG = "NextMarker";
    String TOPIC_URL_TAG = "TopicURL";

    String MESSAGE_LIST_TAG = "Messages";
    String MESSAGE_TAG = "Message";
    String PRIORITY_TAG = "Priority";
    String MESSAGE_ID_TAG = "MessageId";
    String MESSAGE_GROUP_ID_TAG = "MessageGroupId";

    String ENDPOINT_TAG = "Endpoint";
    String NOTIFY_STRATEGY_TAG = "NotifyStrategy";
    String SUBSCRIPTION_NAME_TAG = "SubscriptionName";
    String TOPIC_OWNER_TAG = "TopicOwner";
    String SUBSCRIPTION_STATUS = "State";
    String NOTIFY_CONTENT_FORMAT_TAG = "NotifyContentFormat";
    String SUBSCRIPTION_URL_TAG = "SubscriptionURL";
    String FILTER_TAG_TAG = "FilterTag";

    String RECEIPT_HANDLE_LIST_TAG = "ReceiptHandles";
    String RECEIPT_HANDLE_TAG = "ReceiptHandle";
    String MESSAGE_BODY_TAG = "MessageBody";
    String MESSAGE_BODY_MD5_TAG = "MessageBodyMD5";
    String ENQUEUE_TIME_TAG = "EnqueueTime";
    String NEXT_VISIBLE_TIME_TAG = "NextVisibleTime";
    String FIRST_DEQUEUE_TIME_TAG = "FirstDequeueTime";
    String DEQUEUE_COUNT_TAG = "DequeueCount";
    String MESSAGE_ATTRIBUTES_TAG = "MessageAttributes";

    String MESSAGE_TAG_TAG = "MessageTag";
    String DYSMS_TAG = "DYSMS";
    String DM_TAG = "DM";

    String ERROR_LIST_TAG = "Errors";
    String ERROR_TAG = "Error";
    String ERROR_CODE_TAG = "Code";
    String ERROR_MESSAGE_TAG = "Message";
    String ERROR_REQUEST_ID_TAG = "RequestId";
    String ERROR_HOST_ID_TAG = "HostId";
    String MESSAGE_ERROR_CODE_TAG = "ErrorCode";
    String MESSAGE_ERROR_MESSAGE_TAG = "ErrorMessage";

    String OPEN_SERVICE_ORDER_TAG = "OrderId";

    String ACCOUNT_ID_TAG = "AccountId";

    String PARAM_WAIT_SECONDS = "waitseconds";

    String SUBSCRIPTION = "subscriptions";

    /**
     * https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    String ALIYUN_AK_ENV_KEY = "ALIBABA_CLOUD_ACCESS_KEY_ID";
    String ALIYUN_SK_ENV_KEY = "ALIBABA_CLOUD_ACCESS_KEY_SECRET";

    String IDPT_AK_ENV_KEY = "CLOUD_ACCESS_KEY_ID";
    String IDPT_SK_ENV_KEY = "CLOUD_ACCESS_KEY_SECRET";

    Long MAX_MESSAGE_SIZE = 65536L;
    Long DEFAULT_MESSAGE_RETENTION_PERIOD = 86400L;
    Long MAX_MESSAGE_RETENTION_PERIOD = 86400L;
    Long MIN_MESSAGE_RETENTION_PERIOD = 60L;

    String DEFAULT_NOTIFY_CONTENT_TYPE = "XML";

    int MIN_IMPORTANCE = 1;
    int MAX_IMPORTANCE = 16;
}
