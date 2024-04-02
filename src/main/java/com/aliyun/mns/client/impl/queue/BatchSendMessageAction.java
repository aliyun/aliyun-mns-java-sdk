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

package com.aliyun.mns.client.impl.queue;

import com.aliyun.mns.client.impl.AbstractAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ExceptionResultParser;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.request.queue.BatchSendMessageRequest;
import com.aliyun.mns.model.serialize.queue.ErrorMessageListDeserializer;
import com.aliyun.mns.model.serialize.queue.MessageListDeserializer;
import com.aliyun.mns.model.serialize.queue.MessageListSerializer;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;
import static com.aliyun.mns.common.MNSConstants.LOCATION_MESSAGES;

public class BatchSendMessageAction extends
    AbstractAction<BatchSendMessageRequest, List<Message>> {

    public BatchSendMessageAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.POST, "BatchSendMessage", client, credentials,
            endpoint);
    }

    @Override
    protected RequestMessage buildRequest(BatchSendMessageRequest reqObject)
        throws ClientException {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setResourcePath(reqObject.getRequestPath() + "/"
            + LOCATION_MESSAGES);
        MessageListSerializer serializer = new MessageListSerializer();
        try {
            InputStream is = serializer.serialize(reqObject.getMessages(),
                DEFAULT_CHARSET);
            requestMessage.setContent(is);
            requestMessage.setContentLength(is.available());
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), this.getUserRequestId(), e);
        }
        return requestMessage;
    }

    @Override
    protected ResultParser<List<Message>> buildResultParser() {
        return new ResultParser<List<Message>>() {
            public List<Message> parse(ResponseMessage response)
                throws ResultParseException {
                MessageListDeserializer deserializer = new MessageListDeserializer();
                try {
                    return deserializer.deserialize(response
                        .getContent());
                } catch (Exception e) {
                    throw new ResultParseException("Unmarshal error,cause by:"
                        + e.getMessage(), e);
                }
            }
        };
    }

    @Override
    protected ResultParser<Exception> buildExceptionParser() {
        return new ResultParser<Exception>() {
            public Exception parse(ResponseMessage response)
                throws ResultParseException {
                ErrorMessageListDeserializer deserializer = new ErrorMessageListDeserializer();
                try {
                    return deserializer.deserialize(response.getContent());
                } catch (Exception e) {
                    return new ExceptionResultParser(getUserRequestId()).parse(response); //TODO right?
                }
            }
        };
    }
}