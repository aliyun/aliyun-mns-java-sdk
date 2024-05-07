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

import java.nio.charset.Charset;

public abstract class BaseMessage {

    private String requestId;
    private String messageId;
    private String messageBodyMD5;
    private byte[] messageBodyBytes;

    public BaseMessage() {
        this.requestId = null;
        this.messageId = null;
        this.messageBodyMD5 = null;
        this.messageBodyBytes = null;
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
     * 设置消息体
     *
     * @param messageBodyBytes message body bytes
     */

    protected void setMessageBodyBytes(byte[] messageBodyBytes) {
        this.messageBodyBytes = messageBodyBytes;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (messageId != null) {
            sb.append("MessageID:" + this.messageId + ",");
        }

        if (messageBodyMD5 != null) {
            sb.append("MessageMD5:" + this.messageBodyMD5 + ",");
        }

        if (requestId != null) {
            sb.append("RequestID:" + this.requestId + ",");
        }
        return sb.toString();
    }
}
