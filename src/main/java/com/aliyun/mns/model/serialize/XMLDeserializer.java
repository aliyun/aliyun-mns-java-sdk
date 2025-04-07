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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.model.ErrorMessageResult;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.MessageSystemPropertyName;
import com.aliyun.mns.model.MessageSystemPropertyValue;
import com.aliyun.mns.model.PropertyType;
import com.aliyun.mns.model.SystemPropertyType;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERRORCODE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERRORMESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_SYSTEM_PROPERTY_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_NAME_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_TYPE_TAG;
import static com.aliyun.mns.common.MNSConstants.PROPERTY_VALUE_TAG;
import static com.aliyun.mns.common.MNSConstants.SYSTEM_PROPERTIES_TAG;
import static com.aliyun.mns.common.MNSConstants.USER_PROPERTIES_TAG;

public abstract class XMLDeserializer<T> extends BaseXMLSerializer<T> implements Deserializer<T> {

    protected String safeGetElementContent(Element root, String tagName,
        String defaultValue) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes != null) {
            Node node = nodes.item(0);
            if (node == null) {
                return defaultValue;
            } else {
                return node.getTextContent();
            }
        }
        return defaultValue;
    }

    protected Element safeGetElement(Element root, String tagName) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes != null) {
            Node node = nodes.item(0);
            if (node == null) {
                return null;
            } else {
                return (Element)node;
            }
        }
        return null;
    }

    protected List<Element> safeGetElements(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element)node);
            }
        }
        return elements;
    }

    protected ErrorMessageResult parseErrorMessageResult(Element root) {
        ErrorMessageResult result = new ErrorMessageResult();
        String errorCode = safeGetElementContent(root, MESSAGE_ERRORCODE_TAG,
            null);
        result.setErrorCode(errorCode);

        String errorMessage = safeGetElementContent(root,
            MESSAGE_ERRORMESSAGE_TAG, null);
        result.setErrorMessage(errorMessage);
        return result;
    }

    protected void safeAddPropertiesToMessage(Element root, Message message) {
        Element userPropertiesElement = safeGetElement(root, USER_PROPERTIES_TAG);
        if (userPropertiesElement != null) {
            Map<String, MessagePropertyValue> userProperties = message.getUserProperties();
            if (userProperties == null) {
                userProperties = new HashMap<String, MessagePropertyValue>();
                message.setUserProperties(userProperties);
            }

            for (Element propertyValueElement : safeGetElements(userPropertiesElement, MESSAGE_PROPERTY_TAG)) {
                String name = safeGetElementContent(propertyValueElement, PROPERTY_NAME_TAG, null);
                String value = safeGetElementContent(propertyValueElement, PROPERTY_VALUE_TAG, null);
                String type = safeGetElementContent(propertyValueElement, PROPERTY_TYPE_TAG, null);

                if (name != null && value != null && type != null) {
                    PropertyType typeEnum = PropertyType.valueOf(type);
                    // 如果是二进制类型，需要base64解码
                    if (typeEnum == PropertyType.BINARY) {
                        try {
                            byte[] decodedBytes = Base64.decodeBase64(value);
                            value = new String(decodedBytes, DEFAULT_CHARSET);
                        } catch (UnsupportedEncodingException e) {
                            throw new ClientException("Not support enconding:"
                                + DEFAULT_CHARSET, null, e);
                        }
                    }
                    MessagePropertyValue propertyValue = new MessagePropertyValue(PropertyType.valueOf(type), value);
                    userProperties.put(name, propertyValue);
                }
            }
        }
    }

    protected void safeAddSystemPropertiesToMessage(Element root, Message message) {
        Element systemPropertiesElement = safeGetElement(root, SYSTEM_PROPERTIES_TAG);
        if (systemPropertiesElement != null) {
            for (Element propertyValueElement : safeGetElements(systemPropertiesElement, MESSAGE_SYSTEM_PROPERTY_TAG)) {
                String name = safeGetElementContent(propertyValueElement, PROPERTY_NAME_TAG, null);
                String value = safeGetElementContent(propertyValueElement, PROPERTY_VALUE_TAG, null);
                String type = safeGetElementContent(propertyValueElement, PROPERTY_TYPE_TAG, null);

                if (name != null && value != null && type != null) {
                    SystemPropertyType systemPropertyType = SystemPropertyType.valueOf(type);
                    MessageSystemPropertyValue propertyValue = new MessageSystemPropertyValue(systemPropertyType,
                        value);
                    MessageSystemPropertyName propertyName = MessageSystemPropertyName.getByValue(name);
                    message.putSystemProperty(propertyName, propertyValue);
                }
            }
        }
    }
}
