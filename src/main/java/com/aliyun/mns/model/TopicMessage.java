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

public abstract class TopicMessage extends BaseMessage {
    public static enum BodyType {
        STRING, BASE64
    }

    private String messageTag;

    public TopicMessage() {
        super();
    }

    /**
     * 设置消息体，二进制类型
     *
     * @param messageBody message body
     */
    public void setMessageBody(byte[] messageBody) {
        setBaseMessageBody(messageBody);
    }

    /**
     * 设置消息体，文本类型，文本编码utf-8
     *
     * @param messageBody message body
     */
    public void setMessageBody(String messageBody) {
        setBaseMessageBody(messageBody);

    }

    /**
     * 获取二进制消息体
     *
     * @return message body
     */
    public byte[] getMessageBodyAsBytes() {
        return getMessageBodyBytes();
    }

    public String getMessageTag() {
        return messageTag;
    }

    /**
     * 最多16个字符
     *
     * @param messageTag tag
     */
    public void setMessageTag(String messageTag) {
        this.messageTag = messageTag;
    }

}
