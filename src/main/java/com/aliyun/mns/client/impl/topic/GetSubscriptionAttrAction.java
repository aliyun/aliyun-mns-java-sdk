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
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.request.topic.GetSubscriptionAttrRequest;
import com.aliyun.mns.model.serialize.topic.SubscriptionDeserializer;
import java.net.URI;

public class GetSubscriptionAttrAction extends AbstractAction<GetSubscriptionAttrRequest, SubscriptionMeta> {
    public GetSubscriptionAttrAction(ServiceClient client, ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.GET, "GetSubscriptionAttribute", client, credentials, endpoint);
    }

    @Override
    public RequestMessage buildRequest(GetSubscriptionAttrRequest request) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setResourcePath(request.getRequestPath());
        return requestMessage;
    }

    @Override
    protected ResultParser<SubscriptionMeta> buildResultParser() {
        return new ResultParser<SubscriptionMeta>() {
            @Override
            public SubscriptionMeta parse(ResponseMessage response) throws ResultParseException {
                SubscriptionDeserializer deserializer = new SubscriptionDeserializer();
                try {
                    return deserializer.deserialize(response.getContent());
                } catch (Exception e) {
                    logger.warn("Unmarshal error,cause by:" + e.getMessage());
                    throw new ResultParseException("Unmarshal error,cause by:"
                        + e.getMessage(), e);
                }
            }
        };
    }
}
