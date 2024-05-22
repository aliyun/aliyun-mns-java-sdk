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

package com.aliyun.mns.model.serialize.topic;

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_MD5_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ID_TAG;

public class TopicMessageDeserializer extends XMLDeserializer<TopicMessage> {
    private TopicMessage.BodyType messageType;

    public TopicMessageDeserializer(TopicMessage.BodyType type) {
        this.messageType = type;
    }

    @Override
    public TopicMessage deserialize(InputStream stream) throws Exception {
        Document doc = getDocumentBuilder().parse(stream);

        Element root = doc.getDocumentElement();
        return parseMessage(root);
    }

    private TopicMessage parseMessage(Element root) throws ClientException {
        TopicMessage message = null;
        switch (messageType) {
            case BASE64:
                message = new Base64TopicMessage();
                break;
            case STRING:
                message = new RawTopicMessage();
                break;
        }

        String messageId = safeGetElementContent(root, MESSAGE_ID_TAG, null);
        message.setMessageId(messageId);

        String messageBodyMD5 = safeGetElementContent(root,
            MESSAGE_BODY_MD5_TAG, null);
        message.setMessageBodyMD5(messageBodyMD5);

        return message;
    }
}
