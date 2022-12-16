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
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.model.request.queue.DeleteMessageRequest;
import java.net.URI;

import static com.aliyun.mns.common.MNSConstants.LOCATION_MESSAGES;

public class DeleteMessageAction extends
    AbstractAction<DeleteMessageRequest, Void> {

    public DeleteMessageAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.DELETE, "DeleteMessage", client, credentials, endpoint);
    }

    @Override
    protected RequestMessage buildRequest(DeleteMessageRequest reqObject)
        throws ClientException {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setResourcePath(reqObject.getRequestPath() + "/"
            + LOCATION_MESSAGES + "?ReceiptHandle="
            + reqObject.getReceiptHandle());

        return requestMessage;
    }

    @Override
    protected ResultParser<Void> buildResultParser() {
        return null;
    }
}
