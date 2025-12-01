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
import com.aliyun.mns.model.BaseAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.request.topic.PublishMessageRequest;
import com.aliyun.mns.model.serialize.XMLSerializer;
import com.aliyun.mns.model.serialize.XmlUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.DM_TAG;
import static com.aliyun.mns.common.MNSConstants.DYSMS_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ATTRIBUTES_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_GROUP_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_SYSTEM_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG_TAG;
import static com.aliyun.mns.common.MNSConstants.SYSTEM_PROPERTIES_TAG;
import static com.aliyun.mns.common.MNSConstants.USER_PROPERTIES_TAG;

public class TopicMessageSerializer extends XMLSerializer<PublishMessageRequest> {

    private static Gson gson = null;

    private synchronized Gson getGson() {
        if (gson == null) {
            GsonBuilder b = new GsonBuilder();
            b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
            BooleanSerializer serializer = new BooleanSerializer();
            b.registerTypeAdapter(Boolean.class, serializer);
            b.registerTypeAdapter(boolean.class, serializer);
            gson = b.create();
        }
        return gson;
    }

    @Override
    public InputStream serialize(PublishMessageRequest request, String encoding) throws Exception {
        Document doc = getDocumentBuilder().newDocument();

        TopicMessage msg = request.getMessage();
        Element root = doc.createElementNS(DEFAULT_XML_NAMESPACE, MESSAGE_TAG);
        doc.appendChild(root);

        Element node = safeCreateContentElement(doc, MESSAGE_BODY_TAG, msg.getOriginalMessageBody(), "");
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, MESSAGE_TAG_TAG, msg.getMessageTag(), null);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreatePropertiesNode(doc, msg.getUserProperties(), USER_PROPERTIES_TAG, MESSAGE_PROPERTY_TAG);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreatePropertiesNode(doc, msg.getSystemProperties(), SYSTEM_PROPERTIES_TAG, MESSAGE_SYSTEM_PROPERTY_TAG);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, MESSAGE_GROUP_ID_TAG,
            msg.getMessageGroupId(), null);
        if (node != null) {
            root.appendChild(node);
        }

        MessageAttributes messageAttributes = request.getMessageAttributes();
        if (messageAttributes != null) {
            Element attributesNode = doc.createElement(MESSAGE_ATTRIBUTES_TAG);
            root.appendChild(attributesNode);
            appendAttributeElement(doc, attributesNode, DYSMS_TAG, messageAttributes.getDysmsAttributes());
            appendAttributeElement(doc, attributesNode, DM_TAG, messageAttributes.getDmAttributes());
        }

        String xml = XmlUtil.xmlNodeToString(doc, encoding);
        return new ByteArrayInputStream(xml.getBytes(encoding));
    }

    private void appendAttributeElement(Document doc, Element parent, String tag, BaseAttributes attributes) {
        if (attributes != null) {
            Element node = safeCreateContentElement(doc, tag, attributes.toJson(getGson()), null);
            if (node != null) {
                parent.appendChild(node);
            }
        }
    }

}
