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
import java.util.Date;
import org.apache.commons.codec.binary.Base64;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

public final class Message extends BaseMessage {
    public static enum MessageBodyType {
        BASE64, RAW_STRING
    }

    private String receiptHandle;
    private Integer priority;
    private Date enqueueTime;
    private Date nextVisibleTime;
    private Date firstDequeueTime;
    private Integer dequeueCount;
    private Integer delaySeconds;
    private ErrorMessageResult errorMessage;

    public Message() {
        super();
    }

    public Message(String messageBody) {
        super();
        setMessageBody(messageBody);
    }

    public Message(byte[] messageBody) {
        super();
        setMessageBody(messageBody);
    }

    /**
     * 获取消息延时，单位是秒
     *
     * @return delay second
     */
    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * 设置消息延时，单位是秒
     *
     * @param delaySeconds delay seconds
     */
    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    /**
     * 获取消息句柄
     *
     * @return receipt handle
     */
    public String getReceiptHandle() {
        return receiptHandle;
    }

    /**
     * 设置消息句柄
     *
     * @param receiptHandle receipt handle
     */
    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    /**
     * 获取消息入队时间
     *
     * @return enqueue time
     */
    public Date getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    /**
     * 获取消息下次可见时间
     *
     * @return next visible time
     */
    public Date getNextVisibleTime() {
        return nextVisibleTime;
    }

    public void setNextVisibleTime(Date nextVisibleTime) {
        this.nextVisibleTime = nextVisibleTime;
    }

    /**
     * 获取消息第一次入队时间
     *
     * @return date
     */
    public Date getFirstDequeueTime() {
        return firstDequeueTime;
    }

    public void setFirstDequeueTime(Date firstDequeueTime) {
        this.firstDequeueTime = firstDequeueTime;
    }

    /**
     * 获取消息出队次数
     *
     * @return count
     */
    public Integer getDequeueCount() {
        return dequeueCount;
    }

    public void setDequeueCount(int dequeueCount) {
        this.dequeueCount = dequeueCount;
    }

    /**
     * 设置消息体，二进制类型
     *
     * @param messageBody message body
     */
    public void setMessageBody(byte[] messageBody) {
        setMessageBody(messageBody, MessageBodyType.BASE64);
    }

    /**
     * 设置消息体，二进制类型
     * MessageBodyType 为 RAW_STRING时，原String仅支持UTF-8编码
     *
     * @param messageBody message body
     * @param bodyType    body type
     */
    public void setMessageBody(byte[] messageBody, MessageBodyType bodyType) {
        if (bodyType == MessageBodyType.BASE64) {
            byte[] encodeBase64 = Base64.encodeBase64(messageBody);
            setBaseMessageBody(encodeBase64);
        } else {
            setBaseMessageBody(messageBody);
        }
    }

    /**
     * 设置消息体，文本类型，做Base64编码
     *
     * @param messageBody message body
     */
    public void setMessageBody(String messageBody) {
        setMessageBody(messageBody, MessageBodyType.BASE64);
    }

    /**
     * 设置消息体，指定消息体的类型
     * MessageBodyType 为 RawString 时，使用UTF-8编码
     *
     * @param messageBody message body
     * @param bodyType    body type
     */
    public void setMessageBody(String messageBody, MessageBodyType bodyType) {
        if (bodyType == MessageBodyType.BASE64) {
            try {
                byte[] encodeBase64 = Base64.encodeBase64(messageBody.getBytes(DEFAULT_CHARSET));
                setBaseMessageBody(encodeBase64);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Not support encoding: " + DEFAULT_CHARSET);
            }
        } else { // Raw String, maybe Base64 already
            try {
                setBaseMessageBody(messageBody.getBytes(DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Not support encoding: " + DEFAULT_CHARSET);
            }
        }
    }

    /**
     * 获取消息体，文本类型
     *
     * @return message body
     */
    public String getMessageBody() {
        return getMessageBodyAsString();
    }

    /**
     * 获取Base64编码的消息体
     *
     * @return message body
     */
    public String getMessageBodyAsBase64() {
        if (getMessageBodyBytes() == null)
            return null;
        return new String(getMessageBodyBytes());
    }

    /**
     * 获取文本消息体, 文本编码UTF-8
     *
     * @return message body
     */
    public String getMessageBodyAsString() {
        return getMessageBodyAsString(DEFAULT_CHARSET);
    }

    public String getMessageBodyAsString(String charSet) {
        byte[] messageBodyAsBytes = getMessageBodyBytes();
        if (messageBodyAsBytes == null)
            return null;
        try {
            return new String(Base64.decodeBase64(messageBodyAsBytes), charSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Not support encoding: " + charSet);
        }
    }

    /**
     * 获取文本，UTF-8编码
     *
     * @return message body
     */
    public String getMessageBodyAsRawString() {
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
     * 获取二进制消息体
     *
     * @return message body
     */
    public byte[] getMessageBodyAsBytes() {
        byte[] messageBodyAsBytes = getMessageBodyBytes();
        return Base64.decodeBase64(messageBodyAsBytes);
    }

    /**
     * 获取二进制消息体，仅支持转码为UTF－8
     *
     * @return message body
     */
    public byte[] getMessageBodyAsRawBytes() {
        return getMessageBodyBytes();
    }

    /**
     * 获取消息的优先级
     *
     * @return priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * 设置消息的优先级
     *
     * @param priority priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ErrorMessageResult getErrorMessageDetail() {
        return this.errorMessage;
    }

    public boolean isErrorMessage() {
        return errorMessage != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString());

        if (this.getMessageBodyAsString() != null) {
            sb.append("MessageBody:\"");
            sb.append(getMessageBodyAsString());
            sb.append("\"");
            sb.append(",");
        }

        if (this.receiptHandle != null) {
            sb.append("ReceiptHandle:\"");
            sb.append(this.receiptHandle);
            sb.append("\"");
            sb.append(",");
        }

        sb.append("DequeueCount:\"");
        sb.append(this.dequeueCount);
        sb.append("\"");
        sb.append(",");

        if (this.enqueueTime != null) {
            sb.append("EnqueueTime:\"");
            sb.append(this.enqueueTime);
            sb.append("\"");
            sb.append(",");
        }
        if (this.firstDequeueTime != null) {
            sb.append("FirstDequeueTime:\"");
            sb.append(this.firstDequeueTime);
            sb.append("\"");
            sb.append(",");
        }

        if (this.nextVisibleTime != null) {
            sb.append("NextVisibleTime:\"");
            sb.append(this.nextVisibleTime);
            sb.append("\"");
            sb.append(",");
        }

        sb.append("Priority:\"");
        sb.append(this.priority);
        sb.append("\"");

        return sb.toString();
    }

    public ErrorMessageResult getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessageResult errorMessage) {
        this.errorMessage = errorMessage;
    }
}
