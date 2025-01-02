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

package com.aliyun.mns.client;

import com.aliyun.mns.client.impl.topic.CreateTopicAction;
import com.aliyun.mns.client.impl.topic.DeleteTopicAction;
import com.aliyun.mns.client.impl.topic.GetSubscriptionAttrAction;
import com.aliyun.mns.client.impl.topic.GetTopicAttrAction;
import com.aliyun.mns.client.impl.topic.ListSubscriptionAction;
import com.aliyun.mns.client.impl.topic.PublishMessageAction;
import com.aliyun.mns.client.impl.topic.SetSubscriptionAttrAction;
import com.aliyun.mns.client.impl.topic.SetTopicAttrAction;
import com.aliyun.mns.client.impl.topic.SubscribeAction;
import com.aliyun.mns.client.impl.topic.UnsubscribeAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.model.AttributesValidationResult;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.request.topic.CreateTopicRequest;
import com.aliyun.mns.model.request.topic.DeleteTopicRequest;
import com.aliyun.mns.model.request.topic.GetSubscriptionAttrRequest;
import com.aliyun.mns.model.request.topic.GetTopicAttrRequest;
import com.aliyun.mns.model.request.topic.ListSubscriptionRequest;
import com.aliyun.mns.model.request.topic.PublishMessageRequest;
import com.aliyun.mns.model.request.topic.SetSubscriptionAttrRequest;
import com.aliyun.mns.model.request.topic.SetTopicAttrRequest;
import com.aliyun.mns.model.request.topic.SubscribeRequest;
import com.aliyun.mns.model.request.topic.UnsubscribeRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudTopic {
    /**
     * log4j object
     */
    public static Logger logger = LoggerFactory.getLogger(CloudTopic.class);
    /**
     * object connect to MNS service
     */
    private ServiceClient serviceClient;
    /**
     * topic url, ie: http://uid.mns.region.aliyuncs.com/topics/topicName
     */
    private String topicURL;
    /**
     * object content user auth info
     */
    private ServiceCredentials credentials;
    /**
     * user mns endpoint, ie: http://uid.mns.region.aliyuncs.com/
     */
    private URI endpoint;

    private String accountId;
    private String region;

    private Map<String, String> customHeaders = null;

    /**
     * @param topicName,   topic name
     * @param client,      ServiceClient object
     * @param credentials, ServiceCredentials object
     * @param endpoint,    user mns endpoint, ie: http://uid.mns.region.aliyuncs.com/
     */
    protected CloudTopic(String topicName, ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        this.serviceClient = client;
        this.credentials = credentials;
        this.endpoint = endpoint;

        if (StringUtils.isEmpty(topicName)) {
            throw new NullPointerException("TopicName can not be empty.");
        }

        String host = endpoint.getHost();
        String[] hostPieces = host.split("\\.");
        this.accountId = hostPieces[0];
        this.region = hostPieces[2].split("-internal")[0];

        String uri = endpoint.toString();
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        uri += MNSConstants.TOPIC_PREFIX + topicName;
        this.topicURL = uri;
    }

    void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    /**
     * get topic name from topic url
     *
     * @return topic name
     */
    private String getTopicName() {
        String topicName = null;
        if (topicURL.startsWith(this.endpoint.toString())) {
            topicName = topicURL
                .substring(this.endpoint.toString().length() + 1 + MNSConstants.TOPIC_PREFIX.length());
        }

        // erase start "/"
        while (topicName != null && !topicName.trim().isEmpty()
            && topicName.startsWith("/")) {
            topicName = topicName.substring(1);
        }

        if (topicName == null || topicName.trim().isEmpty()) {
            logger.warn("topic name is null or empty");
            throw new NullPointerException("Topic Name can not be null.");
        }

        return topicName;
    }

    /**
     * get topic url
     *
     * @return topic url
     */
    public String getTopicURL() {
        return topicURL;
    }

    /**
     * create topic with default topic meta
     *
     * @return topic url
     */
    public String create() throws ServiceException {
        String topicName = this.getTopicName();
        TopicMeta meta = new TopicMeta();
        meta.setTopicName(topicName);
        meta.setTopicURL(this.topicURL);
        return create(meta);
    }

    /**
     * create topic with special topic meta
     *
     * @param meta, topic meta data
     * @return topic url
     */
    public String create(TopicMeta meta) throws ServiceException {
        CreateTopicAction action = new CreateTopicAction(serviceClient, credentials, endpoint);
        CreateTopicRequest request = new CreateTopicRequest();
        request.setRequestPath(this.topicURL);
        String topicName = getTopicName();
        if (meta == null) {
            meta = new TopicMeta();
            meta.setTopicName(topicName);
            meta.setTopicURL(this.topicURL);
            logger.debug("topic meta is null, we use default meta");
        }

        if (meta.getTopicName() == null || meta.getTopicName().trim().isEmpty()) {
            meta.setTopicName(topicName);
            meta.setTopicURL(this.topicURL);
            logger.debug("topic name in meta is null or empty, we get it from topic url");
        }

        if (!meta.getTopicName().equals(topicName)) {
            logger.warn("TopicName conflict between meta topic name and  topic url offered");
            throw new ClientException("TopicName conflict between meta topic name and  topic url offered",
                action.getUserRequestId());
        }

        request.setTopicMeta(meta);
        request.setRequestPath(MNSConstants.TOPIC_PREFIX + topicName);
        return action.executeWithCustomHeaders(request, customHeaders);

    }

    /**
     * async set topic attribute with given meta and callback object
     *
     * @param meta,     tpoic meta data
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<Void> asyncSetAttribute(TopicMeta meta, AsyncCallback<Void> callback) throws ServiceException {
        SetTopicAttrAction action = new SetTopicAttrAction(serviceClient, credentials, endpoint);
        SetTopicAttrRequest request = new SetTopicAttrRequest();
        request.setTopicMeta(meta);
        request.setRequestPath(MNSConstants.QUEUE_PREFIX + meta.getTopicName());
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * get topic attribute
     *
     * @return topic meta data
     */
    public TopicMeta getAttribute() throws ServiceException {
        GetTopicAttrAction action = new GetTopicAttrAction(serviceClient, credentials, endpoint);
        GetTopicAttrRequest request = new GetTopicAttrRequest();
        request.setRequestPath(topicURL);
        TopicMeta meta = action.executeWithCustomHeaders(request, customHeaders);
        meta.setTopicURL(topicURL);
        return meta;
    }

    /**
     * set tpoic attribute with given meta
     *
     * @param meta, topic meta data
     */
    public void setAttribute(TopicMeta meta) throws ServiceException {
        SetTopicAttrAction action = new SetTopicAttrAction(serviceClient, credentials, endpoint);
        SetTopicAttrRequest request = new SetTopicAttrRequest();
        request.setTopicMeta(meta);
        request.setRequestPath(MNSConstants.TOPIC_PREFIX + meta.getTopicName());
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async get topic attribute
     *
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<TopicMeta> asyncGetAttribute(AsyncCallback<TopicMeta> callback) throws ServiceException {
        GetTopicAttrAction action = new GetTopicAttrAction(serviceClient, credentials, endpoint);
        GetTopicAttrRequest request = new GetTopicAttrRequest();
        request.setRequestPath(topicURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * delete topic
     */
    public void delete() throws ServiceException {
        DeleteTopicAction action = new DeleteTopicAction(serviceClient, credentials, endpoint);
        DeleteTopicRequest request = new DeleteTopicRequest();
        request.setRequestPath(topicURL);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async delete topic
     *
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<Void> asyncDelete(AsyncCallback<Void> callback) throws ServiceException {
        DeleteTopicAction action = new DeleteTopicAction(serviceClient, credentials, endpoint);
        DeleteTopicRequest request = new DeleteTopicRequest();
        request.setRequestPath(topicURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * subscribe this topic
     *
     * @param meta, SubscriptionMeta data
     * @return, subscription url
     */
    public String subscribe(SubscriptionMeta meta) throws ServiceException {
        if(StringUtils.isEmpty(meta.getSubscriptionName())){
            throw new NullPointerException("subscriptionName can not be empty.");
        }
        SubscribeRequest request = new SubscribeRequest();
        SubscribeAction action = new SubscribeAction(serviceClient, credentials, endpoint);
        request.setMeta(meta);
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + meta.getSubscriptionName());
        String url = action.executeWithCustomHeaders(request, customHeaders);
        return url;
    }

    /**
     * async subscribe this topic
     *
     * @param meta,     SubscriptionMeta data
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */

    public AsyncResult<String> asyncSubscribe(SubscriptionMeta meta, AsyncCallback<String> callback)
        throws ServiceException {
        if(StringUtils.isEmpty(meta.getSubscriptionName())){
            throw new NullPointerException("subscriptionName can not be empty.");
        }
        SubscribeRequest request = new SubscribeRequest();
        SubscribeAction action = new SubscribeAction(serviceClient, credentials, endpoint);
        request.setMeta(meta);
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + meta.getSubscriptionName());
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * set subscription attribute
     *
     * @param meta, SubscriptionMeta data
     */
    public void setSubscriptionAttr(SubscriptionMeta meta) throws ServiceException {
        if(StringUtils.isEmpty(meta.getSubscriptionName())){
            throw new NullPointerException("subscriptionName can not be empty.");
        }
        SetSubscriptionAttrRequest request = new SetSubscriptionAttrRequest();
        SetSubscriptionAttrAction action = new SetSubscriptionAttrAction(serviceClient, credentials, endpoint);
        request.setMeta(meta);
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + meta.getSubscriptionName());
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async set subscription attribute
     *
     * @param meta,     SubscriptionMeta data
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<Void> asyncSetSubscriptionAttr(SubscriptionMeta meta, AsyncCallback<Void> callback)
        throws ServiceException {
        if(StringUtils.isEmpty(meta.getSubscriptionName())){
            throw new NullPointerException("subscriptionName can not be empty.");
        }
        SetSubscriptionAttrRequest request = new SetSubscriptionAttrRequest();
        SetSubscriptionAttrAction action = new SetSubscriptionAttrAction(serviceClient, credentials, endpoint);
        request.setMeta(meta);
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + meta.getSubscriptionName());
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * get subscription attribute
     *
     * @param subscriptionName, subscription name
     * @return SubscriptionMeta data
     */
    public SubscriptionMeta getSubscriptionAttr(String subscriptionName) throws ServiceException {
        GetSubscriptionAttrRequest request = new GetSubscriptionAttrRequest();
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + subscriptionName);
        GetSubscriptionAttrAction action = new GetSubscriptionAttrAction(serviceClient, credentials, endpoint);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async get subscription attribute
     *
     * @param subscriptionName, subscription name
     * @param callback,         user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<SubscriptionMeta> asyncGetSubscriptionAttr(String subscriptionName,
        AsyncCallback<SubscriptionMeta> callback) throws ServiceException {
        GetSubscriptionAttrRequest request = new GetSubscriptionAttrRequest();
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + subscriptionName);
        GetSubscriptionAttrAction action = new GetSubscriptionAttrAction(serviceClient, credentials, endpoint);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * unsubscribe this topic
     *
     * @param subscriptionName, subscription name
     */
    public void unsubscribe(String subscriptionName) throws ServiceException {
        UnsubscribeRequest request = new UnsubscribeRequest();
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + subscriptionName);

        UnsubscribeAction action = new UnsubscribeAction(serviceClient, credentials, endpoint);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async unsubscribe
     *
     * @param subscriptionName, subscription name
     * @param callback,         user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<Void> asyncUnsubscribe(String subscriptionName, AsyncCallback<Void> callback)
        throws ServiceException {
        UnsubscribeRequest request = new UnsubscribeRequest();
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION + "/" + subscriptionName);

        UnsubscribeAction action = new UnsubscribeAction(serviceClient, credentials, endpoint);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * list topic subscription
     *
     * @param prefix,    subscription name prefis
     * @param marker,    subscription start marker
     * @param retNumber, return number
     * @param withMeta,  true return full SubscriptionMeta, false return only subscription url
     * @return SubscriptionMeta list
     */
    private PagingListResult<SubscriptionMeta> listSubscriptions(String prefix, String marker,
        Integer retNumber, boolean withMeta) throws ServiceException {
        ListSubscriptionRequest request = new ListSubscriptionRequest();
        ListSubscriptionAction action = new ListSubscriptionAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + MNSConstants.SUBSCRIPTION);
        request.setMarker(marker);
        request.setPrefix(prefix);
        request.setMaxRet(retNumber);
        request.setWithMeta(withMeta);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * list topic subscription
     *
     * @param prefix,    subscription name prefis
     * @param marker,    subscription start marker
     * @param retNumber, return number
     * @return SubscriptionMeta list
     */
    public PagingListResult<SubscriptionMeta> listSubscriptions(String prefix, String marker, Integer retNumber)
        throws ServiceException {
        return listSubscriptions(prefix, marker, retNumber, true);
    }

    /**
     * list topic subscription
     *
     * @param prefix,    subscription name prefis
     * @param marker,    subscription start marker
     * @param retNumber, return number
     * @return subscription url list
     */
    public PagingListResult<String> listSubscriptionUrls(String prefix, String marker, Integer retNumber)
        throws ServiceException {
        PagingListResult<SubscriptionMeta> list = listSubscriptions(prefix, marker, retNumber, false);
        PagingListResult<String> result = null;
        if (list != null && list.getResult() != null) {
            List<String> tmp = new ArrayList<String>();
            for (SubscriptionMeta meta : list.getResult()) {
                tmp.add(meta.getSubscriptionURL());
            }
            result = new PagingListResult<String>();
            result.setResult(tmp);
            result.setMarker(list.getMarker());
        }
        return result;
    }

    /**
     * generate queue endpoint for subscription
     *
     * @param queueName queueName
     * @return queue endpoint
     */
    public String generateQueueEndpoint(String queueName) {
        return "acs:mns:" + this.region + ":" + this.accountId + ":queues/" + queueName;
    }

    public String generateQueueEndpoint(String queueName, String region) {
        return "acs:mns:" + region + ":" + this.accountId + ":queues/" + queueName;
    }

    /**
     * generate mail endpoint for subscription
     *
     * @param mailAddress mailAddress
     * @return mail endpoint
     */
    public String generateMailEndpoint(String mailAddress) {
        return "mail:directmail:" + mailAddress;
    }

    /**
     * generate dayu endpoint for subscription
     *
     * @param phone phone
     * @return dayu endpoint
     */
    public String generateDayuEndpoint(String phone) {
        return "sms:dayu:" + phone;
    }

    /**
     * generate push endpoint for subscription
     *
     * @param appKey appKey
     * @return push endpoint
     */
    public String generatePushEndpoint(String appKey) {
        return "push:" + appKey;
    }

    /**
     * generate sms endpoint for subscription
     *
     * @param phone phone
     * @return sms endpoint
     */
    public String generateSmsEndpoint(String phone) {
        return "sms:directsms:" + phone;
    }

    public String generateSmsEndpoint() {
        return "sms:directsms:anonymous";
    }

    /**
     * generate batch sms endpoint for subscription
     *
     * @return batch sms endpoint
     */
    public String generateBatchSmsEndpoint() {
        return "sms:directsms:anonymous";
    }

    /**
     * publish message to topic
     *
     * @param msg, message，这里可以使用RawTopicMessage跟Base64TopicMessage作为向服务发消息的结构。
     *             Base64TopicMessage会将消息体进行base64编码。
     *             RawTopicMessage发送的数据是明文可读的串，我们不做任何改动。
     *             如果你是用Base64TopicMessage发送消息的，那么在endpoint端收到消息时，
     *             需要额外做一次base64解码，才能跟你发送的消息相匹配。
     *             <p>
     *             如果接收端包含了邮箱,请使用publishMessage(RawTopicMessage, MessageAttributes)
     * @return message
     */
    public TopicMessage publishMessage(TopicMessage msg) throws ServiceException {
        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + MNSConstants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * publish raw message to topic
     *
     * @param msg,              RawTopicMessage发送的数据是明文可读的串，我们不做任何改动。
     *                          <p>
     *                          如果接收端是邮箱,那么这里的msg就是邮件正文.
     * @param messageAttributes 如果希望被推送到邮箱,那么attributes需要包含发送邮件所必须的几个属性
     * @return message
     */
    public TopicMessage publishMessage(RawTopicMessage msg, MessageAttributes messageAttributes)
        throws ServiceException {
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);

        AttributesValidationResult result = messageAttributes.validate();
        if (!result.isSuccess()) {
            throw new ClientException(result.getMessage(), action.getUserRequestId());
        }

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        request.setMessageAttributes(messageAttributes);
        request.setRequestPath(topicURL + "/" + MNSConstants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * async  publish message, we will do base64 encode for message body before publish it to MNS server.
     * so, when you receive this message, you should do base64 decode before use it.
     *
     * @param msg,      message，这里可以使用RawTopicMessage跟Base64TopicMessage作为向服务发消息的结构。
     *                  但我们推荐使用Base64TopicMessage，它会将消息体进行base64编码后再发送数据。
     *                  RawTopicMessage发送的数据是明文可读的串，我们不做任何改动。
     *                  如果你是用Base64TopicMessage发送消息的，那么在endpoint端收到的消息，
     *                  需要额外做一次base64解码，才能跟你发送的消息相匹配。
     * @param callback, user callback object
     * @return AsyncResult, you can wait result by AsyncResult if you want to do this
     */
    public AsyncResult<TopicMessage> asyncPublishMessage(TopicMessage msg, AsyncCallback<TopicMessage> callback)
        throws ServiceException {
        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + MNSConstants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }
}
