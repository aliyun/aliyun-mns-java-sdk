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

package com.aliyun.mns.client.impl.account;

import com.aliyun.mns.client.impl.AbstractAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.AccountAttributes;
import com.aliyun.mns.model.request.account.GetAccountAttributesRequest;
import com.aliyun.mns.model.serialize.account.AccountAttributesDeserializer;
import java.net.URI;

public class GetAccountAttributesAction extends
    AbstractAction<GetAccountAttributesRequest, AccountAttributes> {

    public GetAccountAttributesAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.GET, "GetAccountAttributes", client, credentials,
            endpoint);
    }

    @Override
    protected RequestMessage buildRequest(GetAccountAttributesRequest reqObject)
        throws ClientException {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setResourcePath("?accountmeta=true");
        return requestMessage;
    }

    @Override
    protected ResultParser<AccountAttributes> buildResultParser() {

        return new ResultParser<AccountAttributes>() {
            @Override
            public AccountAttributes parse(ResponseMessage response)
                throws ResultParseException {
                AccountAttributesDeserializer deserializer = new AccountAttributesDeserializer();
                try {
                    AccountAttributes accountAttributes = deserializer.deserialize(response
                        .getContent());
                    return accountAttributes;
                } catch (Exception e) {
                    throw new ResultParseException("Unmarshal error,cause by:"
                        + e.getMessage(), e);
                }
            }
        };
    }
}
