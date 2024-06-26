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
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.request.queue.BatchPeekMessageRequest;
import com.aliyun.mns.model.serialize.queue.MessageListDeserializer;
import java.net.URI;
import java.util.List;

import static com.aliyun.mns.common.MNSConstants.LOCATION_MESSAGES;
import static com.aliyun.mns.common.MNSConstants.X_HEADER_MNS_REQUEST_ID;

public class BatchPeekMessageAction extends
    AbstractAction<BatchPeekMessageRequest, List<Message>> {

    public BatchPeekMessageAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.GET, "BatchPeekMessage", client, credentials, endpoint);

    }

    @Override
    protected RequestMessage buildRequest(BatchPeekMessageRequest reqObject)
        throws ClientException {
        RequestMessage requestMessage = new RequestMessage();
        String uri = reqObject.getRequestPath() + "/" + LOCATION_MESSAGES
            + "?peekonly=true&numOfMessages=" + reqObject.getBatchSize();

        requestMessage.setResourcePath(uri);
        return requestMessage;
    }

    @Override
    protected ResultParser<List<Message>> buildResultParser() {
        return new ResultParser<List<Message>>() {
            @Override
            public List<Message> parse(ResponseMessage response) throws ResultParseException {
                MessageListDeserializer deserializer = new MessageListDeserializer();
                try {
                    List<Message> msgs = deserializer.deserialize(response.getContent());
                    for (Message msg : msgs) {
                        msg.setRequestId(response.getHeader(X_HEADER_MNS_REQUEST_ID));
                    }
                    return msgs;
                } catch (Exception e) {
                    throw new ResultParseException("Unmarshal error,cause by:"
                        + e.getMessage(), e);
                }
            }
        };
    }
}
