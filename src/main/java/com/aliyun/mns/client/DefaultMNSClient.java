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

import com.aliyun.mns.client.impl.account.GetAccountAttributesAction;
import com.aliyun.mns.client.impl.account.SetAccountAttributesAction;
import com.aliyun.mns.client.impl.commonbuy.OpenServiceAction;
import com.aliyun.mns.client.impl.queue.CreateQueueAction;
import com.aliyun.mns.client.impl.queue.ListQueueAction;
import com.aliyun.mns.client.impl.topic.ListTopicAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.http.ServiceClientFactory;
import com.aliyun.mns.model.AccountAttributes;
import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.request.account.GetAccountAttributesRequest;
import com.aliyun.mns.model.request.account.SetAccountAttributesRequest;
import com.aliyun.mns.model.request.commonbuy.OpenServiceRequest;
import com.aliyun.mns.model.request.queue.CreateQueueRequest;
import com.aliyun.mns.model.request.queue.ListQueueRequest;
import com.aliyun.mns.model.request.topic.ListTopicRequest;
import com.aliyun.mns.model.response.commonbuy.OpenServiceResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aliyun.mns.common.MNSConstants.URI_OPEN_SERVICE;

public class DefaultMNSClient implements MNSClient {
    private static Logger log = LoggerFactory.getLogger(DefaultMNSClient.class);
    private static String OPERATION_LOG_QUEUE_POSTFIX = "-operation-log-queue";

    private boolean isOpen = true;

    // MNS 服务的地址。
    private URI endpoint;

    // 访问MNS服务的client
    private ServiceClient serviceClient;

    // 用户身份信息。
    private ServiceCredentials credentials = new ServiceCredentials();

    private final Map<String, String> customHeaders = new HashMap<String, String>();

    /**
     * 使用指定的MNS Endpoint构造一个新的{@link MNSClient}对象。
     *
     * @param endpoint  MNS服务的Endpoint。
     * @param accessId  访问MNS的Access ID。
     * @param accessKey 访问MNS的Access Key。
     */
    public DefaultMNSClient(String endpoint, String accessId, String accessKey) {
        this(endpoint, accessId, accessKey, null);
    }

    /**
     * 使用指定的MNS Endpoint和配置构造一个新的{@link MNSClient}对象。
     *
     * @param endpoint  MNS服务的Endpoint。
     * @param accessId  访问MNS的Access ID。
     * @param accessKey 访问MNS的Access Key。
     * @param config    客户端配置 {@link ClientConfiguration}。
     */
    public DefaultMNSClient(String endpoint, String accessId, String accessKey,
        ClientConfiguration config) {
        setEndpoint(endpoint);
        this.credentials = new ServiceCredentials(accessId, accessKey);
        if (config == null) {
            config = new ClientConfiguration();
            config.setExceptContinue(false);
        }

        this.serviceClient = ServiceClientFactory.createServiceClient(config);

        if (log.isDebugEnabled()) {
            log.debug("initiated MNSClientImpl,accessId=" + accessId
                + ", endpoint=" + endpoint);
        }
    }

    public int getServiceClientHashCode() {
        return serviceClient.hashCode();
    }

    protected DefaultMNSClient(ServiceCredentials credentials,
        ServiceClient serviceClient, String endpoint) {
        this.serviceClient = serviceClient;
        this.credentials = credentials;
        setEndpoint(endpoint);
    }

    @Override
    public void close() {
        synchronized (this) {
            if (isOpen && serviceClient != null) {
                ServiceClientFactory.closeServiceClient(this.serviceClient);
            }
            isOpen = false;
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void SetAccountAttributes(AccountAttributes accountAttributes) throws ServiceException, ClientException {
        SetAccountAttributesAction action = new SetAccountAttributesAction(serviceClient,
            credentials, endpoint);
        SetAccountAttributesRequest request = new SetAccountAttributesRequest();
        request.setAccountAttributes(accountAttributes);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    @Override
    public AccountAttributes GetAccountAttributes() throws ServiceException, ClientException {
        GetAccountAttributesAction action = new GetAccountAttributesAction(serviceClient,
            credentials, endpoint);
        GetAccountAttributesRequest request = new GetAccountAttributesRequest();
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    @Override
    public CloudQueue getQueueRef(String queueName) {
        CloudQueue cloudQueue = new CloudQueue(queueName, this.serviceClient, this.credentials,
            this.endpoint);
        if (customHeaders.size() > 0) {
            cloudQueue.setCustomHeaders(customHeaders);
        }
        return cloudQueue;
    }

    private void setEndpoint(String endpoint) throws IllegalArgumentException {
        this.endpoint = Utils.getHttpURI(endpoint);
    }

    public void addHeader(String key, String value) {
        customHeaders.put(key, value);
    }

    public AsyncResult<String> createQueueAsync(QueueMeta queueMeta,
        AsyncCallback<String> callback) throws ServiceException {
        CreateQueueAction action = new CreateQueueAction(serviceClient,
            credentials, endpoint);
        CreateQueueRequest request = new CreateQueueRequest();
        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        request.setQueueMeta(queueMeta);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 创建队列
     *
     * @param queueMeta 队列属性
     * @return CloudQueue object
     * @throws ClientException exception
     * @throws ServiceException exception
     */
    public CloudQueue createQueue(QueueMeta queueMeta) throws ClientException,
        ServiceException {
        CloudQueue queue = getQueueRef(queueMeta.getQueueName());
        queue.create(queueMeta);
        return queue;
    }

    @Override
    public TransactionQueue getTransQueueRef(String queueName, TransactionChecker checker) {
        CloudQueue queue = new CloudQueue(queueName, this.serviceClient, this.credentials,
            this.endpoint);
        CloudQueue opLogQueue = new CloudQueue(queueName + OPERATION_LOG_QUEUE_POSTFIX, this.serviceClient, this.credentials,
            this.endpoint);
        return new TransactionQueue(queue, opLogQueue, checker, TransactionQueue.DEFAULT_LIFE_TIME_IN_SECONDS,
            TransactionQueue.DEFAULT_DELAY＿TIME_IN_SECONDS);
    }

    @Override
    public TransactionQueue createTransQueue(QueueMeta queueMeta, TransactionChecker checker, long lifeTime,
        long delayTime) throws ClientException,
        ServiceException {
        if (delayTime <= 0 || lifeTime <= 0) {
            throw new IllegalArgumentException("delayTime(" + delayTime
                + ") or lifetime(" + lifeTime + ") should be bigger than 0");
        }

        CloudQueue queue = new CloudQueue(queueMeta.getQueueName(), this.serviceClient, this.credentials, this.endpoint);
        queueMeta.setMessageRetentionPeriod(lifeTime);
        queueMeta.setDelaySeconds(delayTime);
        queue.create(queueMeta);

        //create operation log queue.
        QueueMeta opLogQueueMeta = new QueueMeta();
        opLogQueueMeta.setQueueName(queueMeta.getQueueName() + OPERATION_LOG_QUEUE_POSTFIX);
        opLogQueueMeta.setPollingWaitSeconds(queueMeta.getPollingWaitSeconds());

        CloudQueue opLogQueue = new CloudQueue(opLogQueueMeta.getQueueName(),
            this.serviceClient, this.credentials, this.endpoint);
        opLogQueue.create(opLogQueueMeta);

        TransactionQueue transQueue = new TransactionQueue(queue, opLogQueue, checker, lifeTime, delayTime);
        return transQueue;
    }

    @Override
    public TransactionQueue createTransQueue(QueueMeta queueMeta, TransactionChecker checker) throws ClientException,
        ServiceException {
        return this.createTransQueue(queueMeta, checker, TransactionQueue.DEFAULT_LIFE_TIME_IN_SECONDS,
            TransactionQueue.DEFAULT_DELAY＿TIME_IN_SECONDS);
    }

    @Override
    public TransactionQueue createTransQueue(QueueMeta queueMeta) throws ClientException,
        ServiceException {
        return this.createTransQueue(queueMeta, null, TransactionQueue.DEFAULT_LIFE_TIME_IN_SECONDS,
            TransactionQueue.DEFAULT_DELAY＿TIME_IN_SECONDS);
    }

    @Override
    public CloudPullTopic createPullTopic(TopicMeta topicMeta, Vector<String> queueNameList, boolean needCreateQueue,
        QueueMeta queueMetaTemplate) throws ClientException, ServiceException {
        if (queueNameList == null || queueNameList.size() <= 0) {
            throw new IllegalArgumentException("queueNameList should not be null or empty.");
        }
        Vector<CloudQueue> queueList = new Vector<CloudQueue>();
        if (needCreateQueue) {
            QueueMeta queueMeta = null;
            if (queueMetaTemplate != null) {
                queueMeta = queueMetaTemplate;
            } else {
                queueMeta = new QueueMeta();
                queueMeta.setPollingWaitSeconds(30);
                //add some default settings;
            }
            for (int i = 0; i < queueNameList.size(); i++) {
                String queueName = queueNameList.get(i);
                queueMeta.setQueueName(queueName);
                CloudQueue queue = new CloudQueue(queueName, this.serviceClient, this.credentials, this.endpoint);
                queue.create(queueMeta);
                queueList.add(queue);
            }
        } else {
            for (int i = 0; i < queueNameList.size(); i++) {
                String queueName = queueNameList.get(i);
                CloudQueue queue = new CloudQueue(queueName, this.serviceClient, this.credentials,
                    this.endpoint);
                queueList.add(queue);
            }
        }
        CloudTopic rawTopic = this.createTopic(topicMeta);
        return new CloudPullTopic(rawTopic, queueNameList, queueList);
    }

    @Override
    public CloudPullTopic createPullTopic(TopicMeta topicMeta, Vector<String> queueNameList, boolean needCreateQueue,
        QueueMeta queueMetaTemplate, Vector<String> tagList)
        throws ClientException, ServiceException {
        if (queueNameList == null || queueNameList.size() <= 0) {
            throw new IllegalArgumentException("queueNameList should not be null or empty.");
        }
        if (tagList == null || tagList.size() != queueNameList.size()) {
            throw new IllegalArgumentException("Size of tagList should be equal with queueNameList.");
        }
        Vector<CloudQueue> queueList = new Vector<CloudQueue>();
        if (needCreateQueue) {
            QueueMeta queueMeta = null;
            if (queueMetaTemplate != null) {
                queueMeta = queueMetaTemplate;
            } else {
                queueMeta = new QueueMeta();
                queueMeta.setPollingWaitSeconds(30);
                //add some default settings;
            }
            for (int i = 0; i < queueNameList.size(); i++) {
                String queueName = queueNameList.get(i);
                queueMeta.setQueueName(queueName);
                CloudQueue queue = new CloudQueue(queueName, this.serviceClient, this.credentials, this.endpoint);
                queue.create(queueMeta);
                queueList.add(queue);
            }
        } else {
            for (int i = 0; i < queueNameList.size(); i++) {
                String queueName = queueNameList.get(i);
                CloudQueue queue = new CloudQueue(queueName, this.serviceClient, this.credentials,
                    this.endpoint);
                queueList.add(queue);
            }
        }
        CloudTopic rawTopic = this.createTopic(topicMeta);
        return new CloudPullTopic(rawTopic, queueNameList, queueList, tagList);
    }

    @Override
    public CloudPullTopic createPullTopic(TopicMeta topicMeta,
        Vector<String> queueNameList) throws ClientException, ServiceException {
        return this.createPullTopic(topicMeta, queueNameList, false, null);
    }

    @Override
    public PagingListResult<String> listQueueURL(String prefix, String marker,
        Integer retNumber) throws ClientException, ServiceException {
        PagingListResult<String> results = new PagingListResult<String>();
        PagingListResult<QueueMeta> list = this.listQueue(prefix, marker,
            retNumber, false);
        if (list != null && list.getResult() != null) {
            List<String> queues = new ArrayList<String>();
            for (QueueMeta meta : list.getResult()) {
                queues.add(meta.getQueueURL());
            }
            if (list.getMarker() != null) {
                results.setMarker(list.getMarker());
            }
            results.setResult(queues);
            return results;
        }
        return results;
    }

    @Override
    public PagingListResult<QueueMeta> listQueue(String prefix, String marker,
        Integer retNumber) throws ClientException, ServiceException {
        return this.listQueue(prefix, marker, retNumber, true);
    }

    private PagingListResult<QueueMeta> listQueue(String prefix, String marker,
        Integer retNumber, boolean withMeta) throws ClientException,
        ServiceException {
        ListQueueAction action = new ListQueueAction(serviceClient,
            credentials, endpoint);
        ListQueueRequest request = new ListQueueRequest();
        request.setRequestPath("/queues");
        request.setMarker(marker);
        request.setPrefix(prefix);
        request.setMaxRet(retNumber);
        request.setWithMeta(withMeta);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    public AsyncResult<PagingListResult<QueueMeta>> asyncListQueue(String prefix, String marker,
        Integer retNumber, boolean withMeta,
        AsyncCallback<PagingListResult<QueueMeta>> callback) throws ClientException,
        ServiceException {
        ListQueueAction action = new ListQueueAction(serviceClient,
            credentials, endpoint);
        ListQueueRequest request = new ListQueueRequest();
        request.setRequestPath("/queues");
        request.setMarker(marker);
        request.setPrefix(prefix);
        request.setMaxRet(retNumber);
        request.setWithMeta(withMeta);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    @Override
    public CloudTopic getTopicRef(String topicName) {
        CloudTopic cloudTopic = new CloudTopic(topicName, this.serviceClient, this.credentials, this.endpoint);
        if (customHeaders.size() > 0) {
            cloudTopic.setCustomHeaders(customHeaders);
        }
        return cloudTopic;
    }

    @Override
    public CloudTopic createTopic(TopicMeta meta) throws ServiceException {
        CloudTopic topic = getTopicRef(meta.getTopicName());
        topic.create(meta);
        return topic;
    }

    @Override
    public PagingListResult<TopicMeta> listTopic(String prefix, String marker,
        Integer retNumber) throws ClientException, ServiceException {
        return listTopic(prefix, marker, retNumber, true);
    }

    private PagingListResult<TopicMeta> listTopic(String prefix, String marker,
        Integer retNumber, boolean withMeta) throws ClientException,
        ServiceException {
        ListTopicAction action = new ListTopicAction(this.serviceClient, this.credentials, this.endpoint);
        ListTopicRequest request = new ListTopicRequest();
        request.setRequestPath(MNSConstants.TPOIC_PREFIX.split("/")[0]);
        request.setMarker(marker);
        request.setPrefix(prefix);
        request.setMaxRet(retNumber);
        if (withMeta) {
            request.setWithMeta(withMeta);
        }
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    @Override
    public PagingListResult<String> listTopicURL(String prefix, String marker,
        Integer retNumber) throws ClientException, ServiceException {
        PagingListResult<TopicMeta> results = listTopic(prefix, marker, retNumber, false);
        PagingListResult<String> ret = new PagingListResult<String>();

        if (results != null) {
            List<String> topics = new ArrayList<String>();
            for (TopicMeta meta : results.getResult()) {
                topics.add(meta.getTopicURL());
            }

            ret.setResult(topics);
            ret.setMarker(results.getMarker());
        }
        return ret;
    }

    @Override
    public OpenServiceResponse openService() throws ServiceException {
        OpenServiceAction action = new OpenServiceAction(this.serviceClient, this.credentials, this.endpoint);
        OpenServiceRequest request = new OpenServiceRequest();
        request.setRequestPath(URI_OPEN_SERVICE);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

}
