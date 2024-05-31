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

package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudPullTopic;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.TopicMeta;
import java.util.Vector;
/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 */
public class CloudPullTopicDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start CloudPullTopicDemo");

        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        MNSClient client = account.getMNSClient();

        // build consumer name list.
        Vector<String> consumerNameList = new Vector<String>();
        String consumerName1 = "consumer001";
        String consumerName2 = "consumer002";
        String consumerName3 = "consumer003";
        String consumerNameAll = "consumerAll";
        consumerNameList.add(consumerName1);
        consumerNameList.add(consumerName2);
        consumerNameList.add(consumerName3);
        consumerNameList.add(consumerNameAll);
        Vector<String> filterTagList = new Vector<String>();
        filterTagList.add("filterTag1");
        filterTagList.add("filterTag2");
        filterTagList.add("filterTag3");
        filterTagList.add("");
        QueueMeta queueMetaTemplate = new QueueMeta();
        queueMetaTemplate.setPollingWaitSeconds(30);

        try {
            //producer code:
            // create pull topic which will send message to 3 queues for consumer.
            String topicName = "demo-topic-for-pull";
            TopicMeta topicMeta = new TopicMeta();
            topicMeta.setTopicName(topicName);
            CloudPullTopic pullTopic = client.createPullTopic(topicMeta, consumerNameList, true, queueMetaTemplate, filterTagList);

            //publish message and consume message.
            String messageBody = "broadcast message to all the consumers:hello the world.";
            // if we sent raw message,then should use getMessageBodyAsRawString to parse the message body correctly.
            {
                TopicMessage tMessage = new RawTopicMessage();
                tMessage.setBaseMessageBody(messageBody + "tag1.");
                tMessage.setMessageTag("filterTag1");
                pullTopic.publishMessage(tMessage);
            }

            {
                TopicMessage tMessage = new RawTopicMessage();
                tMessage.setBaseMessageBody(messageBody + "tag2.");
                tMessage.setMessageTag("filterTag2");
                pullTopic.publishMessage(tMessage);
            }

            {
                TopicMessage tMessage = new RawTopicMessage();
                tMessage.setBaseMessageBody(messageBody + "tag3.");
                tMessage.setMessageTag("filterTag3");
                pullTopic.publishMessage(tMessage);
            }

            // consumer code:
            //3 consumers receive the message.
            CloudQueue queueForConsumer1 = client.getQueueRef(consumerName1);
            CloudQueue queueForConsumer2 = client.getQueueRef(consumerName2);
            CloudQueue queueForConsumer3 = client.getQueueRef(consumerName3);
            CloudQueue queueForConsumerAll = client.getQueueRef(consumerNameAll);

            Message consumer1Msg = queueForConsumer1.popMessage(30);
            if (consumer1Msg != null) {
                System.out.println("consumer1 receive message:" + consumer1Msg.getMessageBodyAsRawString());
                queueForConsumer1.deleteMessage(consumer1Msg.getReceiptHandle());
            } else {
                System.out.println("the queue is empty");
            }

            Message consumer2Msg = queueForConsumer2.popMessage(30);
            if (consumer2Msg != null) {
                System.out.println("consumer2 receive message:" + consumer2Msg.getMessageBodyAsRawString());
                queueForConsumer2.deleteMessage(consumer2Msg.getReceiptHandle());
            } else {
                System.out.println("the queue is empty");
            }

            Message consumer3Msg = queueForConsumer3.popMessage(30);
            if (consumer3Msg != null) {
                System.out.println("consumer3 receive message:" + consumer3Msg.getMessageBodyAsRawString());
                queueForConsumer3.deleteMessage(consumer3Msg.getReceiptHandle());
            } else {
                System.out.println("the queue is empty");
            }

            for (int i = 0; i < 3; i++) {
                Message consumerAllMsg = queueForConsumerAll.popMessage(30);
                if (consumer3Msg != null) {
                    System.out.println("consumerAll receive message:" + consumerAllMsg.getMessageBodyAsRawString());
                    queueForConsumerAll.deleteMessage(consumerAllMsg.getReceiptHandle());
                } else {
                    System.out.println("the queue is empty");
                }
            }

            // delete the fullTopic.
            pullTopic.delete();
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            /*you can get more MNS service error code in following link.
              https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
            se.printStackTrace();
        }

        client.close();
        System.out.println("End CloudPullTopicDemo");
    }

}
