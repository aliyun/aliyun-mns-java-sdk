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
    public static final String LOCATION = "Location";
    public static final String LOCATION_MESSAGES = "messages";

    public static final String X_HEADER_MNS_API_VERSION = "x-mns-version";
    public static final String X_HEADER_MNS_API_VERSION_VALUE = "2015-06-06";

    public static final String X_HEADER_MNS_PREFIX = "x-mns-";
    public static final String X_HEADER_MNS_QUEUE_PREFIX = "x-mns-prefix";
    public static final String X_HEADER_MNS_MARKER = "x-mns-marker";
    public static final String X_HEADER_MNS_RET_NUMBERS = "x-mns-ret-number";
    public static final String X_HEADER_MNS_WITH_META = "x-mns-with-meta";
    public static final String X_HEADER_MNS_REQUEST_ID = "x-mns-request-id";

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_CONTENT_TYPE = "text/xml;charset=UTF-8";

    public static final String DEFAULT_XML_NAMESPACE = "http://mns.aliyuncs.com/doc/v1";

    public static final String QUEUE_PREFIX = "queues/";
    public static final String TOPIC_PREFIX = "topics/";

    public static final String URI_OPEN_SERVICE = "commonbuy/openservice";

    public static final String ACCOUNT_TAG = "Account";
    public static final String QUEUE_TAG = "Queue";
    public static final String TOPIC_TAG = "Topic";
    public static final String QUEUE_NAME_TAG = "QueueName";
    public static final String TOPIC_NAME_TAG = "TopicName";
    public static final String SUBSCRIPTION_TAG = "Subscription";
    public static final String DELAY_SECONDS_TAG = "DelaySeconds";
    public static final String MAX_MESSAGE_SIZE_TAG = "MaximumMessageSize";
    public static final String MESSAGE_RETENTION_PERIOD_TAG = "MessageRetentionPeriod";
    public static final String VISIBILITY_TIMEOUT = "VisibilityTimeout";
    public static final String ACTIVE_MESSAGES_TAG = "ActiveMessages";
    public static final String INACTIVE_MESSAGES_TAG = "InactiveMessages";
    public static final String DELAY_MESSAGES_TAG = "DelayMessages";
    public static final String LASTMODIFYTIME_TAG = "LastModifyTime";
    public static final String CREATE_TIME_TAG = "CreateTime";
    public static final String POLLING_WAITSECONDS_TAG = "PollingWaitSeconds";
    public static final String MESSAGE_COUNT_TAG = "MessageCount";
    public static final String LOGGING_BUCKET_TAG = "LoggingBucket";
    public static final String LOGGING_ENABLED_TAG = "LoggingEnabled";

    public static final String QUEUE_URL_TAG = "QueueURL";
    public static final String NEXT_MARKER_TAG = "NextMarker";
    public static final String TOPIC_URL_TAG = "TopicURL";

    public static final String MESSAGE_LIST_TAG = "Messages";
    public static final String MESSAGE_TAG = "Message";
    public static final String PRIORITY_TAG = "Priority";
    public static final String MESSAGE_ID_TAG = "MessageId";

    public static final String ENDPOINT_TAG = "Endpoint";
    public static final String NOTIFY_STRATEGY_TAG = "NotifyStrategy";
    public static final String SUBSCRIPTION_NAME_TAG = "SubscriptionName";
    public static final String TOPIC_OWNER_TAG = "TopicOwner";
    public static final String SUBSCRIPTION_STATUS = "State";
    public static final String NOTIFY_CONTENT_FORMAT_TAG = "NotifyContentFormat";
    public static final String SUBSCRIPTION_URL_TAG = "SubscriptionURL";
    public static final String FILTER_TAG_TAG = "FilterTag";

    public static final String RECEIPT_HANDLE_LIST_TAG = "ReceiptHandles";
    public static final String RECEIPT_HANDLE_TAG = "ReceiptHandle";
    public static final String MESSAGE_BODY_TAG = "MessageBody";
    public static final String MESSAGE_BODY_MD5_TAG = "MessageBodyMD5";
    public static final String ENQUEUE_TIME_TAG = "EnqueueTime";
    public static final String NEXT_VISIBLE_TIME_TAG = "NextVisibleTime";
    public static final String FIRST_DEQUEUE_TIME_TAG = "FirstDequeueTime";
    public static final String DEQUEUE_COUNT_TAG = "DequeueCount";
    public static final String MESSAGE_ATTRIBUTES_TAG = "MessageAttributes";
    public static final String DIRECT_MAIL_TAG = "DirectMail";
    public static final String MESSAGE_TAG_TAG = "MessageTag";
    public static final String DAYU_TAG = "Dayu";
    public static final String SMS_TAG = "DirectSMS";
    public static final String WEBSOCKET_TAG = "WebSocket";
    public static final String PUSH_TAG = "Push";

    public static final String ERROR_LIST_TAG = "Errors";
    public static final String ERROR_TAG = "Error";
    public static final String ERROR_CODE_TAG = "Code";
    public static final String ERROR_MESSAGE_TAG = "Message";
    public static final String ERROR_REQUEST_ID_TAG = "RequestId";
    public static final String ERROR_HOST_ID_TAG = "HostId";
    public static final String MESSAGE_ERRORCODE_TAG = "ErrorCode";
    public static final String MESSAGE_ERRORMESSAGE_TAG = "ErrorMessage";

    public static final String OPEN_SERVICE_ORDER_TAG = "OrderId";

    public static final String ACCOUNT_ID_TAG = "AccountId";

    public static final String PARAM_WAITSECONDS = "waitseconds";

    public static final String SUBSCRIPTION = "subscriptions";

    /**
     * https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    public static final String ALIYUN_AK_ENV_KEY = "ALIBABA_CLOUD_ACCESS_KEY_ID";
    public static final String ALIYUN_SK_ENV_KEY = "ALIBABA_CLOUD_ACCESS_KEY_SECRET";

    public static final Long MAX_MESSAGE_SIZE = 65536L;
    public static final Long DEFAULT_MESSAGE_RETENTION_PERIOD = 86400L;
    public static final Long MAX_MESSAGE_RETENTION_PERIOD = 86400L;
    public static final Long MIN_MESSAGE_RETENTION_PERIOD = 60L;

    public static final String DEFAULT_NOTIFY_CONTENT_TYPE = "XML";

    public static final int MIN_IMPORTANCE = 1;
    public static final int MAX_IMPORTANCE = 16;
}
