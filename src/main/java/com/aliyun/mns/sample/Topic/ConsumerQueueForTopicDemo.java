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

package com.aliyun.mns.sample.Topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 在 topic 模型下，queue 有三个类型，xml、json、simple，在 base 64 加密下不一样，详见下文
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 *           mns.msgBodyBase64Switch=true/false
 */
public class ConsumerQueueForTopicDemo {
    /**
     * 是否做 base64 编码
     */
    private static final Boolean IS_BASE64 = Boolean.valueOf(ServiceSettings.getMNSPropertyValue("msgBodyBase64Switch","false"));

    public static void main(String[] args) {
        String QUEUE_NAME = "TestQueue";

        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();
        CloudQueue queue = client.getQueueRef(QUEUE_NAME);

        try {
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
                System.out.println("message handle: " + message.getReceiptHandle());
                System.out.println("message body: " + message.getOriginalMessageBody());
                System.out.println("message body real data: " + getMessageBodyData(message));
                System.out.println("message id: " + message.getMessageId());
                System.out.println("message dequeue count:" + message.getDequeueCount());
                //<<to add your special logic.>>

                //remember to  delete message when consume message successfully.
                queue.deleteMessage(message.getReceiptHandle());
                System.out.println("delete message successfully.\n");
            }
        }

        System.out.println("=============end longPollingBatchReceive=============");

    }

    private static String getMessageBodyData(Message message){
        if (message == null){
            return null;
        }
        String originalMessageBody = message.getOriginalMessageBody();

        // 1. 尝试解析为JSON
        try {
            JSONObject object = new JSONObject(originalMessageBody);
            String jsonMessageData = String.valueOf(object.get("Message"));
            System.out.println("message body type: JSON,value:"+jsonMessageData );
            return IS_BASE64? new String(Base64.getDecoder().decode(jsonMessageData), StandardCharsets.UTF_8): jsonMessageData;
        } catch (JSONException ex1) {
            // 不是JSON，继续检查XML
        }

        // 2. 尝试解析为XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(originalMessageBody)));
            Element root = doc.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("Message");
            String content = nodeList.item(0).getTextContent();
            System.out.println("message body type: XML,value:"+content );

            return IS_BASE64? new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8): content;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            // 不是有效的XML
        }

        // 既不是JSON也不是XML，视为普通文本
        System.out.println("message body type: SIMPLE" );
        return IS_BASE64 ? message.getMessageBody() : message.getMessageBodyAsRawString();

    }


    public String safeGetElementContent(Element root, String tagName,
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
}
