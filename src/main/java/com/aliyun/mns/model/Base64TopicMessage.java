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

import org.apache.commons.codec.binary.Base64;

public class Base64TopicMessage extends TopicMessage {
    public Base64TopicMessage() {
        super();
    }

    /**
     * 获取Base64编码的消息体
     * @return message body
     */
    public String getMessageBodyAsBase64() {
        if (getMessageBodyBytes() == null)
            return null;
        return Base64.encodeBase64String(getMessageBodyBytes());
    }

    /**
     * 获取消息体，文本类型，获取的文本为消息体的base64编码
     *
     * @return message body
     */
    public String getMessageBody() {
        return getMessageBodyAsBase64();
    }
}
