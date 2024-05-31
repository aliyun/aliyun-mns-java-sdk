package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;


/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 *           mns.msgBodyBase64Switch=true/false
 */
public class PublishMessageDemo {
    private static final Boolean IS_BASE64 = Boolean.valueOf(ServiceSettings.getMNSPropertyValue("msgBodyBase64Switch","false"));

    public static void main(String[] args) {
        String topicName = "TestTopic";
        String message = "hello world!";

        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();

        publishMsg(client, topicName, message);

        client.close();
    }

    private static void publishMsg(MNSClient client, String topicName, String message) {

        CloudTopic topic = client.getTopicRef(topicName);
        TopicMessage msg = IS_BASE64 ? new Base64TopicMessage() : new RawTopicMessage();
        try {
            msg.setMessageBody(message);
            // 可选。设置该条发布消息的filterTag
            //msg.setMessageTag("filterTag");
            TopicMessage publishResultMsg = topic.publishMessage(msg);
            System.out.println("message publish.");
            System.out.println("reqId:"+publishResultMsg.getRequestId());
            System.out.println("msgId:"+publishResultMsg.getMessageId());
            System.out.println("md5:"+publishResultMsg.getMessageBodyMD5());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("publishMsg error");
        }
    }
}
