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

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import java.util.List;

/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 *           mns.msgBodyBase64Switch=true/false
 */
public class ReceiveMessageDemo {
    /**
     * 是否做 base64 编码
     */
    private static final Boolean IS_BASE64 = Boolean.valueOf(ServiceSettings.getMNSPropertyValue("msgBodyBase64Switch","false"));

    public static void main(String[] args) {
        String queueName = "cloud-queue-demo";

        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();
        CloudQueue queue = client.getQueueRef(queueName);

        try {
            // 基础: 单次拉取
            singleReceive(queue);

            // 推荐: 使用的 长轮询批量拉取模型
            longPollingBatchReceive(queue);
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            if (se.getErrorCode().equals("QueueNotExist")) {
                System.out.println("Queue is not exist.Please create queue before use");
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

    private static void longPollingBatchReceive(CloudQueue queue) {
        System.out.println("=============start longPollingBatchReceive=============");

        // 一次性拉取 最多 xx 条消息
        int batchSize = 15;
        // 长轮询时间为 xx s
        int waitSeconds = 15;

        List<Message> messages = queue.batchPopMessage(batchSize, waitSeconds);
        if (messages != null && messages.size() > 0) {

            for (Message message : messages) {
                printMsgAndDelete(queue,message);
            }
        }

        System.out.println("=============end longPollingBatchReceive=============");

    }

    private static void singleReceive(CloudQueue queue) {
        System.out.println("=============start singleReceive=============");

        Message popMsg = queue.popMessage();
        printMsgAndDelete(queue, popMsg);

        System.out.println("=============end singleReceive=============");
    }

    private static void printMsgAndDelete(CloudQueue queue, Message popMsg) {
        if (popMsg != null) {
            System.out.println("message handle: " + popMsg.getReceiptHandle());
            System.out.println("message body: " + (IS_BASE64 ? popMsg.getMessageBody() : popMsg.getMessageBodyAsRawString()));
            System.out.println("message id: " + popMsg.getMessageId());
            System.out.println("message dequeue count:" + popMsg.getDequeueCount());
            //<<to add your special logic.>>

            //remember to  delete message when consume message successfully.
            queue.deleteMessage(popMsg.getReceiptHandle());
            System.out.println("delete message successfully.\n");
        }
    }

}
