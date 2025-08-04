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

package com.aliyun.mns.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

public abstract class BaseMessage {

    private String requestId;
    private String messageId;
    private String messageBodyMD5;
    private byte[] messageBodyBytes;
    private Map<String, MessagePropertyValue> userProperties;
    private Map<String, MessageSystemPropertyValue> systemProperties;
    private String MessageGroupId;

    public BaseMessage() {
        this.requestId = null;
        this.messageId = null;
        this.messageBodyMD5 = null;
        this.messageBodyBytes = null;
        this.userProperties = new ConcurrentHashMap<String, MessagePropertyValue>();
        this.systemProperties = new ConcurrentHashMap<String, MessageSystemPropertyValue>();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 获取消息ID
     *
     * @return message id
     */
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取消息体的MD5
     *
     * @return message body
     */
    public String getMessageBodyMD5() {
        return messageBodyMD5;
    }

    public void setMessageBodyMD5(String messageBodyMD5) {
        this.messageBodyMD5 = messageBodyMD5;
    }

    /**
     * 获取消息体，二进制类型，该方法用于子类
     *
     * @return messageBody
     */
    protected byte[] getMessageBodyBytes() {
        return messageBodyBytes;
    }

    /**
     * 设置消息体
     *
     * @param messageBodyBytes message body bytes
     */

    protected void setMessageBodyBytes(byte[] messageBodyBytes) {
        this.messageBodyBytes = messageBodyBytes;
    }

    /**
     * 设置消息体，二进制类型
     *
     * @param messageBody message body
     */
    public void setBaseMessageBody(byte[] messageBody) {
        setMessageBodyBytes(messageBody);
    }

    /**
     * 设置消息体，文本类型，文本编码utf-8
     *
     * @param messageBody message body
     */
    public void setBaseMessageBody(String messageBody) {
        setMessageBodyBytes(messageBody.getBytes(Charset.forName("utf-8")));
    }

    /**
     * 基于 bytes 获得最原始的 string 值，不受子类影响
     */
    public String getOriginalMessageBody() {
        byte[] messageBodyAsBytes = getMessageBodyBytes();
        if (messageBodyAsBytes == null) {
            return null;
        }
        try {
            return new String(messageBodyAsBytes, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Not support encoding: " + DEFAULT_CHARSET);
        }
    }

    /**
     * 获取消息体，文本类型，获取的文本是否为原始消息，由子类方法决定
     *
     * @return message body
     */
    public abstract String getMessageBody();

    /**
     * 通过文体串来设置消息体
     *
     * @param messageBody message body
     */
    public abstract void setMessageBody(String messageBody);

    /**
     * 通过二进制来设置消息体
     *
     * @param messageBody message body
     */
    public abstract void setMessageBody(byte[] messageBody);

    public Map<String, MessagePropertyValue> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, MessagePropertyValue> userProperties) {
        this.userProperties = userProperties;
    }

    public MessageSystemPropertyValue getSystemProperty(MessageSystemPropertyName key) {
        if (key == null) {
            return null;
        }
        return systemProperties.get(key.getValue());
    }

    public void putSystemProperty(MessageSystemPropertyName keyName, MessageSystemPropertyValue value) {
        systemProperties.put(keyName.getValue(), value);
    }

    public Map<String, MessageSystemPropertyValue> getSystemProperties() {
        return systemProperties;
    }

    public String getMessageGroupId() {
        return MessageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        MessageGroupId = messageGroupId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (messageId != null) {
            sb.append("MessageID:").append(this.messageId).append(",");
        }

        if (messageBodyMD5 != null) {
            sb.append("MessageMD5:").append(this.messageBodyMD5).append(",");
        }

        if (requestId != null) {
            sb.append("RequestID:").append(this.requestId).append(",");
        }
        if (userProperties != null) {
            for (Map.Entry<String, MessagePropertyValue> entry : userProperties.entrySet()) {
                sb.append("Property.").append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
        }
        return sb.toString();
    }
}
