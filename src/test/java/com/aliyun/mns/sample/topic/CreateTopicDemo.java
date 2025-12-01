package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.TopicMeta;
/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.accountendpoint=http://xxxxxxx
 *           mns.regionId=cn-xxxx
 */
public class CreateTopicDemo {

    public static void main(String[] args) {
        String topicName = "TestTopic";

        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

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
