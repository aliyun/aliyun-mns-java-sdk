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

package com.aliyun.mns.model.serialize;

import java.util.Map;

import com.aliyun.mns.model.AbstractMessagePropertyValue;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.PropertyType;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.DELAY_SECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_SYSTEM_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.PRIORITY_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_TYPE_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_VALUE_TAG;
import static com.aliyun.mns.common.MNSConstants.SYSTEM_PROPERTIES_TAG;
import static com.aliyun.mns.common.MNSConstants.USER_PROPERTIES_TAG;

public abstract class XMLSerializer<T> extends BaseXMLSerializer<T> implements Serializer<T> {

    public Element safeCreateContentElement(Document doc, String tagName,
        Object value, String defaultValue) {
        if (value == null && defaultValue == null) {
            return null;
        }

        Element node = doc.createElement(tagName);
        if (value != null) {
            node.setTextContent(value.toString());
        } else {
            node.setTextContent(defaultValue);
        }
        return node;
    }

    public Element serializeMessage(Document doc, Message msg) {
        Element root = doc.createElementNS(DEFAULT_XML_NAMESPACE, MESSAGE_TAG);

        Element node = safeCreateContentElement(doc, MESSAGE_BODY_TAG,
            msg.getOriginalMessageBody(), "");

        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, DELAY_SECONDS_TAG,
            msg.getDelaySeconds(), null);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, PRIORITY_TAG,
            msg.getPriority(), null);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreatePropertiesNode(doc, msg.getUserProperties(), USER_PROPERTIES_TAG, MESSAGE_PROPERTY_TAG);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreatePropertiesNode(doc, msg.getSystemProperties(), SYSTEM_PROPERTIES_TAG,
            MESSAGE_SYSTEM_PROPERTY_TAG);
        if (node != null) {
            root.appendChild(node);
        }

        return root;
    }

    public Element safeCreatePropertiesNode(Document doc, Map<String, ? extends AbstractMessagePropertyValue> map,
        String nodeName, String propertyNodeName) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Element propertiesNode = doc.createElement(nodeName);
        for (Map.Entry<String, ? extends AbstractMessagePropertyValue> entry : map.entrySet()) {
            Element propNode = doc.createElement(propertyNodeName);

            Element nameNode = safeCreateContentElement(doc, PROPERTY_NAME_TAG, entry.getKey(), null);
            if (nameNode != null) {
                propNode.appendChild(nameNode);
            }

            String valueString = entry.getValue().getStringValueByType();
            if (entry.getValue() instanceof MessagePropertyValue) {
                PropertyType propertyType = ((MessagePropertyValue) entry.getValue()).getDataType();
                if (propertyType == PropertyType.BINARY) {
                    // 防止特殊字符，使用base64编码
                    valueString = new String(Base64.encodeBase64(valueString.getBytes()));
                }
            }
            Element valueNode = safeCreateContentElement(doc, PROPERTY_VALUE_TAG,
                valueString, null);
            if (valueNode != null) {
                propNode.appendChild(valueNode);
            }

            Element typeNode = safeCreateContentElement(doc, PROPERTY_TYPE_TAG,
                entry.getValue().getDataTypeString(), null);
            if (typeNode != null) {
                propNode.appendChild(typeNode);
            }

            propertiesNode.appendChild(propNode);
        }
        return propertiesNode;
    }
}
