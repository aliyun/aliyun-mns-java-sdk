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

package com.aliyun.mns.sample.queue;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;

/**
 * 1. 遵循阿里云规范，env 设置
 * ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment
 * -variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 * mns.endpoint=http://xxxxxxx
 * mns.msgBodyBase64Switch=true/false
 */
public class SendMessageDemo {

    /**
     * replace with your queue name
     */
    private static final String QUEUE_NAME = "cloud-queue-demo";

    private static final Boolean IS_BASE64 = Boolean.valueOf(
        ServiceSettings.getMNSPropertyValue("msgBodyBase64Switch", "false"));

    public static void main(String[] args) {
        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        MNSClient client = account.getMNSClient();

        // Demo for send message code, send 10 test message
        try {
            CloudQueue queue = client.getQueueRef(QUEUE_NAME);
            for (int i = 0; i < 10; i++) {
                Message message = new Message();
                String messageValue = "demo_message_body" + i;
                if (IS_BASE64) {
                    // base 64 编码
                    message.setMessageBody(messageValue);
                } else {
                    // 不进行任何编码
                    message.setMessageBodyAsRawString(messageValue);
                }

                Map<String, MessagePropertyValue> userProperties = new HashMap<String, MessagePropertyValue>();
                userProperties.put("key1", new MessagePropertyValue("value1"));
                userProperties.put("key2", new MessagePropertyValue(1));
                message.setUserProperties(userProperties);

                Message putMsg = queue.putMessage(message);
                System.out.println("Send message id is: " + putMsg.getMessageId());
            }
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            if (se.getErrorCode().equals("QueueNotExist")) {
                System.out.println("Queue is not exist.Please create before use");
            } else if (se.getErrorCode().equals("TimeExpired")) {
                System.out.println("The request is time expired. Please check your local machine timeclock");
            }
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }

        client.close();
    }

}
