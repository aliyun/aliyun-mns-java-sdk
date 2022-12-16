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
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.model.request.queue.SetQueueAttrRequest;
import com.aliyun.mns.model.serialize.queue.QueueMetaSerializer;
import java.io.InputStream;
import java.net.URI;

public class SetQueueAttrAction extends
    AbstractAction<SetQueueAttrRequest, Void> {

    public SetQueueAttrAction(ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.PUT, "SetQueueAttributes", client, credentials,
            endpoint);
    }

    @Override
    protected RequestMessage buildRequest(SetQueueAttrRequest reqObject)
        throws ClientException {
        RequestMessage message = new RequestMessage();
        message.setResourcePath(reqObject.getRequestPath() + "?metaoverride=true");
        QueueMetaSerializer serializer = new QueueMetaSerializer();
        try {
            InputStream is = serializer.serialize(reqObject.getQueueMeta(), MNSConstants.DEFAULT_CHARSET);
            message.setContent(is);
            message.setContentLength(is.available());
            return message;
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), this.getUserRequestId(), e);
        }
    }

}
