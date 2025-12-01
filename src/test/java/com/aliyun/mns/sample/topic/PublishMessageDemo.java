package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;

import java.util.HashMap;
import java.util.Map;


/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.accountendpoint=http://xxxxxxx
 *           mns.regionId=cn-xxxx
 *           mns.msgBodyBase64Switch=true/false
 */
public class PublishMessageDemo {
    private static final Boolean IS_BASE64 = Boolean.valueOf(ServiceSettings.getMNSPropertyValue("msgBodyBase64Switch","false"));

    public static void main(String[] args) {
        String topicName = "TestTopic";
        String message = "hello world!";

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

        publishMsg(client, topicName, message);

        client.close();
    }

    private static void publishMsg(MNSClient client, String topicName, String message) {

        CloudTopic topic = client.getTopicRef(topicName);
        TopicMessage msg = IS_BASE64 ? new Base64TopicMessage() : new RawTopicMessage();
        try {
            msg.setMessageBody(message);

            Map<String, MessagePropertyValue> userProperties = new HashMap<String, MessagePropertyValue>();
            userProperties.put("key1", new MessagePropertyValue("value1"));
            userProperties.put("key2", new MessagePropertyValue(1));
            msg.setUserProperties(userProperties);

            // 可选。设置该条发布消息的filterTag
            // 设置后，消息服务MNS在推送消息时会根据标签进行过滤，仅推送消息标签与订阅中指定的过滤标签匹配的消息到指定队列上。
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
