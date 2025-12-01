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

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.accountendpoint=http://xxxxxxx
 *           mns.regionId=cn-xxxx
 *           mns.msgBodyBase64Switch=true/false
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
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

        // Demo for send message code, send 10 test message
        try {
            CloudQueue queue = client.getQueueRef(QUEUE_NAME);
            for (int i = 0; i < 10; i++) {
                Message message = new Message();
                //String messageValue = "demo_message_body" + i;
                // 生成 1~512 KB 随机大小的可读字符串
                int xKB = new Random().nextInt(512) + 1;
                String messageValue = generateRandomReadableString(1);
                System.out.println("Generated message body size: " + xKB + " KB");

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

    // 在类中添加以下方法
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?/";

    /**
     * 生成指定大小（KB）的随机可读字符串
     */
    public static String generateRandomReadableString(int sizeInKB) {
        int sizeInBytes = sizeInKB * 1024;
        StringBuilder sb = new StringBuilder(sizeInBytes);
        Random random = new Random();

        for (int i = 0; i < sizeInBytes; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }
}
