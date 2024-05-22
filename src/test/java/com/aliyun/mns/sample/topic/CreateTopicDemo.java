package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.TopicMeta;

public class CreateTopicDemo {

    public static void main(String[] args) {
        String topicName = "TestTopic";

        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();

        String topicURL = createTopic(client, topicName);
        System.out.println("topic url: " + topicURL);

        client.close();
    }

    private static String createTopic(MNSClient client, String topicName) {
        TopicMeta meta = new TopicMeta();
        meta.setTopicName(topicName);

        try {
            CloudTopic topic = client.createTopic(meta);
            return topic.getTopicURL();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("create topic error, " + e.getMessage());
        }

        return null;
    }
}
