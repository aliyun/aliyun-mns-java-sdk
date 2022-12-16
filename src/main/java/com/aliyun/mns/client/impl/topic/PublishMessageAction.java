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

package com.aliyun.mns.client.impl.topic;

import com.aliyun.mns.client.impl.AbstractAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.request.topic.PublishMessageRequest;
import com.aliyun.mns.model.serialize.topic.TopicMessageDeserializer;
import com.aliyun.mns.model.serialize.topic.TopicMessageSerializer;
import java.io.InputStream;
import java.net.URI;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

public class PublishMessageAction extends AbstractAction<PublishMessageRequest, TopicMessage> {
    private TopicMessage.BodyType messageType;

    public PublishMessageAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.POST, "PublishMessage", client, credentials, endpoint);
        messageType = TopicMessage.BodyType.STRING;

    }

    @Override
    protected RequestMessage buildRequest(PublishMessageRequest reqObject)
        throws ClientException {
        TopicMessage message = reqObject.getMessage();
        if (message instanceof RawTopicMessage) {
            messageType = TopicMessage.BodyType.STRING;
        } else if (message instanceof Base64TopicMessage) {
            messageType = TopicMessage.BodyType.BASE64;
        }

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setResourcePath(reqObject.getRequestPath());
        TopicMessageSerializer serializer = new TopicMessageSerializer();

        try {
            InputStream is = serializer.serialize(reqObject,
                DEFAULT_CHARSET);
            requestMessage.setContent(is);
            requestMessage.setContentLength(is.available());
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), this.getUserRequestId(), e);
        }
        return requestMessage;
    }

    @Override
    protected ResultParser<TopicMessage> buildResultParser() {
        return new ResultParser<TopicMessage>() {
            public TopicMessage parse(ResponseMessage response) throws ResultParseException {

                TopicMessageDeserializer deserializer = new TopicMessageDeserializer(messageType);
                try {
                    return deserializer.deserialize(response.getContent());
                } catch (Exception e) {
                    logger.warn("Unmarshal error,cause by:" + e.getMessage());
                    throw new ResultParseException(
                        "Unmarshal error,cause by:" + e.getMessage(), e);
                }
            }
        };
    }
}
