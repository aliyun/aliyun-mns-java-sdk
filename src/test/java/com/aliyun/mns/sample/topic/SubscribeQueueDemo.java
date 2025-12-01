package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.SubscriptionMeta;

import java.util.Random;

/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.accountendpoint=http://xxxxxxx
 *           mns.regionId=cn-xxxx
 */
public class SubscribeQueueDemo {
    public static void main(String[] args) {
        String topicName = "TestTopic";
        String subQueueName = "TestQueue";

        String regionId = ServiceSettings.getMNSRegion();
        String accountId = ServiceSettings.getMNSPropertyValue("accountId","111111111");


        String subArn = String.format("acs:mns:%s:%s:queues/%s", regionId, accountId, subQueueName);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

        String subUrl = subscribeQueue(client, topicName, subArn);

        System.out.println("subscription url: " + subUrl);
        client.close();
    }

    private static String subscribeQueue(MNSClient client, String topicName, String subArn) {
        CloudTopic topic = client.getTopicRef(topicName);
        try {
            SubscriptionMeta subMeta = new SubscriptionMeta();
            subMeta.setSubscriptionName("QueueEndpoint-"+ (new Random()).nextInt(10000));
            subMeta.setEndpoint(subArn);
//            subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.XML);
            subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
            return topic.subscribe(subMeta);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("subscribe error");
        }
        return null;
    }
}
