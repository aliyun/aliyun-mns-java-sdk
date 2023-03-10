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
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ServiceClient;
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

        if (queueURL == null || queueURL.equals("")) {
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
     * ?????????????????????????????????
     *
     * @return ?????????URL
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public String create() throws ServiceException, ClientException {
        return create(null);
    }

    /**
     * ????????????????????????????????????queueMeta??????
     *
     * @param queueMeta, queue meta data
     * @return ?????????URL
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
     * ??????????????????????????????????????????queueMeta??????
     *
     * @param queueMeta queue meta data
     * @param callback  callback
     * @return ?????????URL
     * @throws ClientException exception
     */
    public AsyncResult<String> asyncCreate(QueueMeta queueMeta, AsyncCallback<String> callback) throws
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
     * ????????????
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
     * ??????????????????
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
     * ?????????????????????
     *
     * @return ????????????
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
     * ???????????????????????????
     *
     * @param callback callback
     * @return ????????????
     * @throws ClientException exception
     */
    public AsyncResult<QueueMeta> asyncGetAttributes(AsyncCallback<QueueMeta> callback) throws ClientException {
        GetQueueAttrAction action = new GetQueueAttrAction(serviceClient,
            credentials, endpoint);
        GetQueueAttrRequest request = new GetQueueAttrRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????
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
     * ????????????????????????
     *
     * @param queueMeta queue meta data
     * @param callback  callback
     * @return result
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncSetAttributes(QueueMeta queueMeta,
        AsyncCallback<Void> callback) throws ClientException {
        SetQueueAttrAction action = new SetQueueAttrAction(serviceClient,
            credentials, endpoint);
        SetQueueAttrRequest request = new SetQueueAttrRequest();
        request.setQueueMeta(queueMeta);
        request.setRequestPath(MNSConstants.QUEUE_PREFIX + queueMeta.getQueueName());
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????, ????????????????????????null
     *
     * @return ??????????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message peekMessage() throws ServiceException, ClientException {
        PeekMessageAction action = new PeekMessageAction(serviceClient,
            credentials, endpoint);
        PeekMessageRequest request = new PeekMessageRequest();
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param callback ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPeekMessage(AsyncCallback<Message> callback)
        throws ClientException {
        PeekMessageAction action = new PeekMessageAction(serviceClient,
            credentials, endpoint);
        PeekMessageRequest request = new PeekMessageRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ???????????????????????????????????????????????????null
     *
     * @param batchSize ?????????????????????????????????
     * @return ??????????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPeekMessage(int batchSize) throws ServiceException,
        ClientException {
        BatchPeekMessageAction action = new BatchPeekMessageAction(
            serviceClient, credentials, endpoint);
        BatchPeekMessageRequest request = new BatchPeekMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param batchSize ?????????????????????????????????
     * @param callback  callback
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPeekMessage(int batchSize,
        AsyncCallback<List<Message>> callback) throws ClientException {
        BatchPeekMessageAction action = new BatchPeekMessageAction(serviceClient,
            credentials, endpoint);
        BatchPeekMessageRequest request = new BatchPeekMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????????????????
     *
     * @param receiptHandle     ????????????
     * @param visibilityTimeout ????????????????????????????????????
     * @return ??????????????????
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
     * ????????????????????????????????????
     *
     * @param receiptHandle     ????????????????????????
     * @param visibilityTimeout ??????????????????????????????????????????
     * @param callback          ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<String> asyncChangeMessageVisibilityTimeout(
        String receiptHandle, int visibilityTimeout,
        AsyncCallback<String> callback) throws ClientException {
        ChangeVisibilityTimeoutAction action = new ChangeVisibilityTimeoutAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????????????????
     *
     * @param receiptHandle     ????????????
     * @param visibilityTimeout ????????????????????????????????????
     * @return ??????????????????????????????????????????????????????
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
     * ????????????????????????????????????
     *
     * @param receiptHandle     ????????????????????????
     * @param visibilityTimeout ??????????????????????????????????????????
     * @param callback          ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncChangeMessageVisibility(
        String receiptHandle, int visibilityTimeout,
        AsyncCallback<Message> callback) throws ClientException {
        ChangeVisibilityAction action = new ChangeVisibilityAction(
            serviceClient, credentials, endpoint);
        ChangeVisibilityTimeoutRequest request = new ChangeVisibilityTimeoutRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        request.setVisibilityTimeout(visibilityTimeout);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ????????????????????????, ???????????????????????????????????????null
     *
     * @return ??????????????????????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message popMessage() throws ServiceException, ClientException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ????????????????????????, ????????????????????????????????????null
     *
     * @param waitSeconds ????????????????????????????????????
     * @return ????????????????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public Message popMessage(int waitSeconds) throws ServiceException, ClientException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        request.setWaitSeconds(waitSeconds);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param callback ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPopMessage(AsyncCallback<Message> callback)
        throws ClientException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????????????????
     *
     * @param waitSeconds ????????????????????????????????????
     * @param callback    ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPopMessage(int waitSeconds, AsyncCallback<Message> callback)
        throws ClientException {
        ReceiveMessageAction action = new ReceiveMessageAction(serviceClient,
            credentials, endpoint);
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setRequestPath(queueURL);
        request.setWaitSeconds(waitSeconds);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????????????????, ????????????????????????????????????null
     *
     * @param batchSize ?????????????????????????????????
     * @return ????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPopMessage(int batchSize) throws ServiceException, ClientException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ??????????????????????????????, ????????????????????????????????????null
     *
     * @param batchSize   ?????????????????????????????????
     * @param waitSeconds ????????????????????????????????????
     * @return ????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public List<Message> batchPopMessage(int batchSize, int waitSeconds)
        throws ServiceException, ClientException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setWaitSeconds(waitSeconds);
        request.setRequestPath(queueURL);
        try {
            return action.executeWithCustomHeaders(request, customHeaders);
        } catch (ServiceException e) {
            if (!isMessageNotExist(e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * ????????????????????????????????????
     *
     * @param batchSize batch size
     * @param callback  callback
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPopMessage(int batchSize,
        AsyncCallback<List<Message>> callback) throws ClientException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ????????????????????????????????????
     *
     * @param batchSize size
     * @param waitSeconds ????????????????????????????????????
     * @param callback    ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPopMessage(int batchSize, int waitSeconds,
        AsyncCallback<List<Message>> callback) throws ClientException {
        BatchReceiveMessageAction action = new BatchReceiveMessageAction(serviceClient,
            credentials, endpoint);
        BatchReceiveMessageRequest request = new BatchReceiveMessageRequest();
        request.setBatchSize(batchSize);
        request.setWaitSeconds(waitSeconds);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ????????????
     *
     * @param receiptHandle ????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public void deleteMessage(String receiptHandle) throws ServiceException,
        ClientException {
        DeleteMessageAction action = new DeleteMessageAction(serviceClient,
            credentials, endpoint);
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * ??????????????????
     *
     * @param receiptHandle ????????????
     * @param callback      ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncDeleteMessage(String receiptHandle,
        AsyncCallback<Void> callback) throws ClientException {
        DeleteMessageAction action = new DeleteMessageAction(serviceClient,
            credentials, endpoint);
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandle(receiptHandle);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????
     *
     * @param receiptHandles ??????????????????
     * @throws ServiceException exception
     * @throws ClientException  exception
     */
    public void batchDeleteMessage(List<String> receiptHandles)
        throws ServiceException, ClientException {
        BatchDeleteMessageAction action = new BatchDeleteMessageAction(serviceClient,
            credentials, endpoint);
        BatchDeleteMessageRequest request = new BatchDeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandles(receiptHandles);
        action.executeWithCustomHeaders(request, customHeaders);
    }

    /**
     * @param receiptHandles ??????????????????
     * @param callback       ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Void> asyncBatchDeleteMessage(List<String> receiptHandles,
        AsyncCallback<Void> callback) throws ClientException {
        BatchDeleteMessageAction action = new BatchDeleteMessageAction(serviceClient,
            credentials, endpoint);
        BatchDeleteMessageRequest request = new BatchDeleteMessageRequest();
        request.setRequestPath(queueURL);
        request.setReceiptHandles(receiptHandles);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ????????????, ?????????????????????????????????????????????????????????????????????base64???????????????????????????SDK?????????
     * ??????????????????????????????????????????????????????????????????????????????base64?????????
     *
     * @param message ??????????????????
     * @return ?????????????????????
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
     * ??????????????????, ?????????????????????????????????????????????????????????????????????base64???????????????????????????SDK?????????
     * ??????????????????????????????????????????????????????????????????????????????base64?????????
     *
     * @param message  ??????????????????
     * @param callback ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<Message> asyncPutMessage(Message message,
        AsyncCallback<Message> callback) throws ClientException {
        SendMessageAction action = new SendMessageAction(serviceClient,
            credentials, endpoint);
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage(message);
        request.setRequestPath(queueURL);
        return action.executeWithCustomHeaders(request, callback, customHeaders);
    }

    /**
     * ??????????????????, ?????????????????????????????????????????????????????????????????????base64???????????????????????????SDK?????????
     * ??????????????????????????????????????????????????????????????????????????????base64?????????
     *
     * @param messages ??????????????????
     * @return ?????????????????????
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
     * ????????????????????????, ?????????????????????????????????????????????????????????????????????base64???????????????????????????SDK?????????
     * ??????????????????????????????????????????????????????????????????????????????base64?????????
     *
     * @param messages ??????????????????
     * @param callback ??????????????????
     * @return ????????????????????????
     * @throws ClientException exception
     */
    public AsyncResult<List<Message>> asyncBatchPutMessage(List<Message> messages,
        AsyncCallback<List<Message>> callback) throws ClientException {
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
        return "MessageNotExist".equals(e.getErrorCode());
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
            if (se.getErrorCode().equals("QueueNotExist")) {// queue does not exist;
                res = false;
            } else {
                // other errors.
                throw se;
            }
        }
        return res;
    }
}
