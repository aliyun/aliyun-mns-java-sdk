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

import com.aliyun.mns.common.utils.BooleanSerializer;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.PushAttributes;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.request.topic.PublishMessageRequest;
import com.aliyun.mns.model.serialize.XMLSerializer;
import com.aliyun.mns.model.serialize.XmlUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.DAYU_TAG;
import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.DIRECT_MAIL_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ATTRIBUTES_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG_TAG;
import static com.aliyun.mns.common.MNSConstants.PUSH_TAG;
import static com.aliyun.mns.common.MNSConstants.SMS_TAG;
import static com.aliyun.mns.common.MNSConstants.WEBSOCKET_TAG;

public class TopicMessageSerializer extends XMLSerializer<PublishMessageRequest> {
    private static Gson gson = null;

    private synchronized Gson getGson() {
        if (gson == null) {
            GsonBuilder b = new GsonBuilder();
            b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
            BooleanSerializer serializer = new BooleanSerializer();
            b.registerTypeAdapter(Boolean.class, serializer);
            b.registerTypeAdapter(boolean.class, serializer);
            b.registerTypeAdapter(PushAttributes.class, new PushAttributes.PushAttributesSerializer());
            gson = b.create();
        }
        return gson;
    }

    @Override
    public InputStream serialize(PublishMessageRequest request, String encoding) throws Exception {
        Document doc = getDocmentBuilder().newDocument();

        TopicMessage msg = request.getMessage();
        Element root = doc.createElementNS(DEFAULT_XML_NAMESPACE, MESSAGE_TAG);
        doc.appendChild(root);

        Element node = safeCreateContentElement(doc, MESSAGE_BODY_TAG,
            msg.getMessageBody(), "");
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, MESSAGE_TAG_TAG, msg.getMessageTag(), null);
        if (node != null) {
            root.appendChild(node);
        }

        MessageAttributes messageAttributes = request.getMessageAttributes();
        if (messageAttributes != null) {
            Element attributesNode = doc.createElement(MESSAGE_ATTRIBUTES_TAG);
            root.appendChild(attributesNode);

            if (messageAttributes.getMailAttributes() != null) {
                node = safeCreateContentElement(doc, DIRECT_MAIL_TAG, messageAttributes.getMailAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }

            if (messageAttributes.getDayuAttributes() != null) {
                node = safeCreateContentElement(doc, DAYU_TAG, messageAttributes.getDayuAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }

            if (messageAttributes.getSmsAttributes() != null) {
                node = safeCreateContentElement(doc, SMS_TAG, messageAttributes.getSmsAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }

            if (messageAttributes.getBatchSmsAttributes() != null) {
                node = safeCreateContentElement(doc, SMS_TAG, messageAttributes.getBatchSmsAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }

            if (messageAttributes.getWebSocketAttributes() != null) {
                node = safeCreateContentElement(doc, WEBSOCKET_TAG, messageAttributes.getWebSocketAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }

            if (messageAttributes.getPushAttributes() != null) {
                node = safeCreateContentElement(doc, PUSH_TAG, messageAttributes.getPushAttributes().toJson(getGson()), null);
                if (node != null) {
                    attributesNode.appendChild(node);
                }
            }
        }

        String xml = XmlUtil.xmlNodeToString(doc, encoding);

        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
