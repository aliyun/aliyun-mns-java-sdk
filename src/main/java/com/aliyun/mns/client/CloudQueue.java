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

import com.aliyun.mns.client.impl.queue.BatchDeleteMessageAction;
import com.aliyun.mns.client.impl.queue.BatchPeekMessageAction;
import com.aliyun.mns.client.impl.queue.BatchReceiveMessageAction;
import com.aliyun.mns.client.impl.queue.BatchSendMessageAction;
import com.aliyun.mns.client.impl.queue.ChangeVisibilityAction;
import com.aliyun.mns.client.impl.queue.ChangeVisibilityTimeoutAction;
import com.aliyun.mns.client.impl.queue.CreateQueueAction;
import com.aliyun.mns.client.impl.queue.DeleteMessageAction;
import com.aliyun.mns.client.impl.queue.DeleteQueueAction;
import com.aliyun.mns.client.impl.queue.GetQueueAttrAction;
import com.aliyun.mns.client.impl.queue.PeekMessageAction;
import com.aliyun.mns.client.impl.queue.ReceiveMessageAction;
import com.aliyun.mns.client.impl.queue.SendMessageAction;
import com.aliyun.mns.client.impl.queue.SetQueueAttrAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.ServiceHandlingRequiredException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.utils.ServiceConstants;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.request.queue.BatchDeleteMessageRequest;
import com.aliyun.mns.model.request.queue.BatchPeekMessageRequest;
import com.aliyun.mns.model.request.queue.BatchReceiveMessageRequest;
import com.aliyun.mns.model.request.queue.BatchSendMessageRequest;
import com.aliyun.mns.model.request.queue.ChangeVisibilityTimeoutRequest;
import com.aliyun.mns.model.request.queue.CreateQueueRequest;
import com.aliyun.mns.model.request.queue.DeleteMessageRequest;
import com.aliyun.mns.model.request.queue.DeleteQueueRequest;
import com.aliyun.mns.model.request.queue.GetQueueAttrRequest;
import com.aliyun.mns.model.request.queue.PeekMessageRequest;
import com.aliyun.mns.model.request.queue.ReceiveMessageRequest;
import com.aliyun.mns.model.request.queue.SendMessageRequest;
import com.aliyun.mns.model.request.queue.SetQueueAttrRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;

public final class CloudQueue {
    private ServiceClient serviceClient;
    private String queueURL;
    private ServiceCredentials credentials;
    private URI endpoint;

    private Map<String, String> customHeaders = null;

    protected CloudQueue(String queueURL, ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        this.serviceClient = client;
        this.credentials = credentials;
        this.endpoint = endpoint;

        if (queueURL == null || "".equals(queueURL)) {
            throw new NullPointerException(
                "QueueURL parameter can not be empty.");
        }
        String uri = endpoint.toString();
        if (queueURL.startsWith(uri)) {
            this.queueURL = queueURL;
        } else {
            if (!uri.endsWith("/")) {
                uri += "/";
            }

            if (queueURL != null) {
                uri += MNSConstants.QUEUE_PREFIX + queueURL;
            }
            this.queueURL = uri;
        }

    }

    void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    /**
     * 创建队列，使用默认属性
     *
     * @return 队列的URL
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public String create() throws ServiceException, ClientException {
        return create(null);
    }

    /**
     * 创建队列，队列属性由参数queueMeta设置
     *
     * @param queueMeta, queue meta data
     * @return 队列的URL
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public String create(QueueMeta queueMeta) throws ServiceException,
        ClientException {

        CreateQueueAction action = new CreateQueueAction(serviceClient,
            credentials, endpoint);
        String queueName = drillQueueName();
        CreateQueueRequest request = new CreateQueueRequest();
        if (queueMeta == null || queueMeta.getQueueName() == null) {
            queueMeta = new QueueMeta();
            queueMeta.setQueueName(queueName);
        } else {
            if (queueMeta.getQueueName() == null
                || queueMeta.getQueueName().isEmpty()) {
                queueMeta.setQueueName(queueName);
            } else {
                if (!queueName.equals(queueMeta.getQueueName())) {
                    throw new ClientException(
                        "QueueName conflict between meta queue name and  queue url offered.",
                        action.getUserRequestId());
                }
            }
        }

        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        request.setQueueMeta(queueMeta);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步创建队列，队列属性由参数queueMeta设置
     *
     * @param queueMeta queue meta data
     * @param callback  callback
     * @return 队列的URL
     * @throws ClientException exception
     */
    public AsyncResult<String> asyncCreate(QueueMeta queueMeta, AsyncCallback<String> callback) throws
        ClientException, ServiceException {

        CreateQueueAction action = new CreateQueueAction(serviceClient,
            credentials, endpoint);
        String queueName = drillQueueName();
        CreateQueueRequest request = new CreateQueueRequest();
        if (queueMeta == null || queueMeta.getQueueName() == null) {
            queueMeta = new QueueMeta();
            queueMeta.setQueueName(queueName);
        } else {
            if (queueMeta.getQueueName() == null
                || queueMeta.getQueueName().isEmpty()) {
                queueMeta.setQueueName(queueName);
            } else {
                if (!queueName.equals(queueMeta.getQueueName())) {
                    throw new ClientException(
                        "QueueName conflict between meta queue name and  queue url offered.",
                        action.getUserRequestId());
                }
            }
        }

        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        request.setQueueMeta(queueMeta);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    private String drillQueueName() {
        String queueName = null;
        if (queueURL.startsWith(this.endpoint.toString())) {
            queueName = queueURL
                .substring(this.endpoint.toString().length() + 1 + MNSConstants.QUEUE_PREFIX.length());
        }

        // erase start "/"
        while (queueName != null && queueName.trim().length() > 0
            && queueName.startsWith("/")) {
            queueName = queueName.substring(1);
        }

        if (queueName == null || queueName.trim().length() == 0) {
            throw new NullPointerException("Queue Name can not be null.");
        }

        return queueName;
    }

    /**
     * 删除队列
     *
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public void delete() throws ServiceException, ClientException {
        DeleteQueueAction action = new DeleteQueueAction(serviceClient,
            credentials, endpoint);
        DeleteQueueRequest request = new DeleteQueueRequest();
        request.setRequestPath(queueURL);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步删除队列
     *
     * @param callback callback
     * @return result
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public AsyncResult<Void> asyncDelete(AsyncCallback<Void> callback) throws ServiceException, ClientException {
        DeleteQueueAction action = new DeleteQueueAction(serviceClient,
            credentials, endpoint);
        DeleteQueueRequest request = new DeleteQueueRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 获取队列的属性
     *
     * @return 队列属性
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public QueueMeta getAttributes() throws ServiceException, ClientException {
        GetQueueAttrAction action = new GetQueueAttrAction(serviceClient,
            credentials, endpoint);
        GetQueueAttrRequest request = new GetQueueAttrRequest();
        request.setRequestPath(queueURL);
        QueueMeta meta = action.executeWithCustomHeaders(request, customHeaders);
        meta.setQueueURL(queueURL);
        return meta;
    }

    /**
     * 异步获取队列的属性
     *
     * @param callback callback
     * @return 队列属性
     * @throws ClientException exception
     */
    public AsyncResult<QueueMeta> asyncGetAttributes(AsyncCallback<QueueMeta> callback)
        throws ClientException, ServiceException {
        GetQueueAttrAction action = new GetQueueAttrAction(serviceClient,
            credentials, endpoint);
        GetQueueAttrRequest request = new GetQueueAttrRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 设置队列属性
     *
     * @param queueMeta, queue meta data
     * @throws ClientException  exception
     * @throws ServiceException exception
     */
    public void setAttributes(QueueMeta queueMeta) throws ClientException,
        ServiceException {
        SetQueueAttrAction action = new SetQueueAttrAction(serviceClient,
            credentials, endpoint);
        SetQueueAttrRequest request = new SetQueueAttrRequest();
        request.setQueueMeta(queueMeta);
        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步设置队列属性
     *
     * @param queueMeta queue meta data
     * @param callback  callback
     * @return result
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncSetAttributes(QueueMeta queueMeta,
        AsyncCallback<Void> callback) throws ClientException, ServiceException {
        SetQueueAttrAction action = new SetQueueAttrAction(serviceClient,
            credentials, endpoint);
        SetQueueAttrRequest request = new SetQueueAttrRequest();
        request.setQueueMeta(queueMeta);
        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 查看队列消息, 消息不存在时返回null
     *
     * @return 查找到的消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message peekMessage() throws ServiceException, ClientException, ServiceHandlingRequiredException {
        PeekMessageAction action = new PeekMessageAction(serviceClient,
            credentials, endpoint);
        PeekMessageRequest request = new PeekMessageRequest();
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {

            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    /**
     * 异步查看队列消息
     *
     * @param callback 异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPeekMessage(AsyncCallback<Message> callback)
        throws ClientException, ServiceException {
        PeekMessageAction action = new PeekMessageAction(serviceClient,
            credentials, endpoint);
        PeekMessageRequest request = new PeekMessageRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 批量查看队列消息，消息不存在时返回null
     *
     * @param batchSize 本次最多查看消息的条数
     * @return 查找到的消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPeekMessage(int batchSize) throws ServiceException,
        ClientException, ServiceHandlingRequiredException {
        BatchPeekMessageAction action = new BatchPeekMessageAction(
            serviceClient, credentials, endpoint);
        BatchPeekMessageRequest request = new BatchPeekMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {

            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    /**
     * 异步批量查看消息
     *
     * @param batchSize 本次最多查看消息的条数
     * @param callback  callback
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPeekMessage(int batchSize,
        AsyncCallback<List<Message>> callback) throws ClientException, ServiceException {
        BatchPeekMessageAction action = new BatchPeekMessageAction(serviceClient,
            credentials, endpoint);
        BatchPeekMessageRequest request = new BatchPeekMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 改变消息的不可见时间
     *
     * @param receiptHandle     消息句柄
     * @param visibilityTimeout 消息不可见时间，单位是秒
     * @return 新的消息句柄
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public String changeMessageVisibilityTimeout(String receiptHandle,
        int visibilityTimeout) throws ServiceException, ClientException {
        ChangeVisibilityTimeoutAction action = new ChangeVisibilityTimeoutAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步改变消息的不可见时间
     *
     * @param receiptHandle     待改变消息的句柄
     * @param visibilityTimeout 新的消息不可见时间，单位是秒
     * @param callback          异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<String> asyncChangeMessageVisibilityTimeout(
        String receiptHandle, int visibilityTimeout,
        AsyncCallback<String> callback) throws ClientException, ServiceException {
        ChangeVisibilityTimeoutAction action = new ChangeVisibilityTimeoutAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 改变消息的不可见时间
     *
     * @param receiptHandle     消息句柄
     * @param visibilityTimeout 消息不可见时间，单位是秒
     * @return 新的消息，保含消息句柄和下次可见时间
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message changeMessageVisibility(String receiptHandle,
        int visibilityTimeout) throws ServiceException, ClientException {
        ChangeVisibilityAction action = new ChangeVisibilityAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步改变消息的不可见时间
     *
     * @param receiptHandle     待改变消息的句柄
     * @param visibilityTimeout 新的消息不可见时间，单位是秒
     * @param callback          异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncChangeMessageVisibility(
        String receiptHandle, int visibilityTimeout,
        AsyncCallback<Message> callback) throws ClientException, ServiceException {
        ChangeVisibilityAction action = new ChangeVisibilityAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 获取队列中的消息, 在队列中没有消息的时候返回null
     *
     * @return 返回队列中的一个消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message popMessage() throws ServiceException, ClientException, ServiceHandlingRequiredException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    private boolean isServiceHandlingRequired(ServiceException e) {
        if (e == null){
            return false;
        }
        String errorCode = e.getErrorCode();
        if (ServiceConstants.ERROR_CODE_QUEUE_NOT_EXIST.equals(errorCode)) {
            return false;
        }
        if (ServiceConstants.ERROR_CODE_TIME_EXPIRED.equals(errorCode)) {
            return false;
        }
        return true;
    }

    /**
     * 获取队列中的消息, 队列中没有消息的时候返回null
     *
     * @param waitSeconds 长轮询等待时间，单位是秒
     * @return 队列中的一个消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message popMessage(int waitSeconds)
        throws ServiceException, ClientException, ServiceHandlingRequiredException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        request.setWaitSeconds(waitSeconds);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {

            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    /**
     * 异步获取队列中的消息
     *
     * @param callback 异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPopMessage(AsyncCallback<Message> callback)
        throws ClientException, ServiceException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 异步获取队列中的消息
     *
     * @param waitSeconds 长轮询等待时间，单位是秒
     * @param callback    异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPopMessage(int waitSeconds, AsyncCallback<Message> callback)
        throws ClientException, ServiceException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        request.setWaitSeconds(waitSeconds);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 批量获取队列中的消息, 队列中没有消息的时候返回null
     *
     * @param batchSize 本次最多获取消息的条数
     * @return 消息列表
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPopMessage(int batchSize)
        throws ServiceException, ClientException, ServiceHandlingRequiredException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    /**
     * 批量获取队列中的消息, 队列中没有消息的时候返回null
     *
     * @param batchSize   本次最多获取消息的条数
     * @param waitSeconds 长轮询等待时间，单位是秒
     * @return 消息列表
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPopMessage(int batchSize, int waitSeconds)
        throws ServiceException, ClientException, ServiceHandlingRequiredException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setWaitSeconds(waitSeconds);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (isMessageNotExist(e)){
                // 没拉到消息，合理
                return null;
            }
            if (isServiceHandlingRequired(e)){
                // 远程服务不可用，需要上游感知并强制捕获异常处理
                throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
            }
            throw e;
        }
    }

    /**
     * 异步批量获取队列中的消息
     *
     * @param batchSize batch size
     * @param callback  callback
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPopMessage(int batchSize,
        AsyncCallback<List<Message>> callback) throws ClientException, ServiceException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 异步批量获取队列中的消息
     *
     * @param batchSize size
     * @param waitSeconds 长轮询等待时间，单位是秒
     * @param callback    异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPopMessage(int batchSize, int waitSeconds,
        AsyncCallback<List<Message>> callback) throws ClientException, ServiceException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setWaitSeconds(waitSeconds);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 删除消息
     *
     * @param receiptHandle 消息句柄
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public void deleteMessage(String receiptHandle) throws ServiceException,
        ClientException, ServiceHandlingRequiredException {
        DeleteMessageAction action = new DeleteMessageAction(serviceClient,
            credentials, endpoint);
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        try {
            action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            // 远程服务不可用，需要上游感知并强制捕获异常处理
            throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
        }
    }

    /**
     * 异步删除消息
     *
     * @param receiptHandle 消息句柄
     * @param callback      异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncDeleteMessage(String receiptHandle,
        AsyncCallback<Void> callback) throws ClientException, ServiceException {
        DeleteMessageAction action = new DeleteMessageAction(serviceClient,
            credentials, endpoint);
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 批量删除消息
     *
     * @param receiptHandles 消息句柄列表
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public void batchDeleteMessage(List<String> receiptHandles)
        throws ServiceException, ClientException, ServiceHandlingRequiredException {
        BatchDeleteMessageAction action = new BatchDeleteMessageAction(serviceClient,
            credentials, endpoint);
        BatchDeleteMessageRequest request = new BatchDeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandles(receiptHandles);
        try {
            action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            // 远程服务不可用，需要上游感知并强制捕获异常处理
            throw new ServiceHandlingRequiredException(e.getMessage(),e,e.getErrorCode(),e.getRequestId(),e.getHostId());
        }
    }

    /**
     * @param receiptHandles 消息句柄列表
     * @param callback       异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncBatchDeleteMessage(List<String> receiptHandles,
        AsyncCallback<Void> callback) throws ClientException, ServiceException {
        BatchDeleteMessageAction action = new BatchDeleteMessageAction(serviceClient,
            credentials, endpoint);
        BatchDeleteMessageRequest request = new BatchDeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandles(receiptHandles);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 发送消息, 消息体在发送到服务端前，我们会对消息体进行一次base64编码，如果你使用本SDK发送，
     * 但用其他方式接收时，需要确认，接收方有无对消息体进行base64解码。
     *
     * @param message 待发送的消息
     * @return 发送成功的消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message putMessage(Message message) throws ServiceException,
        ClientException {
        SendMessageAction action = new SendMessageAction(serviceClient,
            credentials, endpoint);
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage(message);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步发送消息, 消息体在发送到服务端前，我们会对消息体进行一次base64编码，如果你使用本SDK发送，
     * 但用其他方式接收时，需要确认，接收方有无对消息体进行base64解码。
     *
     * @param message  待发送的消息
     * @param callback 异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPutMessage(Message message,
        AsyncCallback<Message> callback) throws ClientException, ServiceException {
        SendMessageAction action = new SendMessageAction(serviceClient,
            credentials, endpoint);
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage(message);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * 批量发送消息, 消息体在发送到服务端前，我们会对消息体进行一次base64编码，如果你使用本SDK发送，
     * 但用其他方式接收时，需要确认，接收方有无对消息体进行base64解码。
     *
     * @param messages 待发送的消息
     * @return 发送成功的消息
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPutMessage(List<Message> messages) throws ServiceException,
        ClientException {
        BatchSendMessageAction action = new BatchSendMessageAction(serviceClient,
            credentials, endpoint);
        BatchSendMessageRequest request = new BatchSendMessageRequest();
        request.setMessages(messages);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * 异步批量发送消息, 消息体在发送到服务端前，我们会对消息体进行一次base64编码，如果你使用本SDK发送，
     * 但用其他方式接收时，需要确认，接收方有无对消息体进行base64解码。
     *
     * @param messages 待发送的消息
     * @param callback 异步回调对象
     * @return 异步结果调用句柄
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPutMessage(List<Message> messages,
        AsyncCallback<List<Message>> callback) throws ClientException, ServiceException {
        BatchSendMessageAction action = new BatchSendMessageAction(serviceClient,
            credentials, endpoint);
        BatchSendMessageRequest request = new BatchSendMessageRequest();
        request.setMessages(messages);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    public String getQueueURL() {
        return queueURL;
    }

    public boolean isMessageNotExist(ServiceException e) {
        return ServiceConstants.ERROR_CODE_MSG_NOT_EXIST.equalsIgnoreCase(e.getErrorCode());
    }

    /**
     * Check if queue is exist already.
     *
     * @return true means queue exists, false means queue does not exist.
     * @throws ServiceException ClientException
     */
    public boolean isQueueExist() throws ServiceException, ClientException {
        boolean res = false;
        try {
            this.getAttributes();
            res = true;  // queue exists if get attributes successfully.
        } catch (ServiceException se) {
            if ("QueueNotExist".equals(se.getErrorCode())) {// queue does not exist;
                res = false;
            } else {
                // other errors.
                throw se;
            }
        }
        return res;
    }
}
