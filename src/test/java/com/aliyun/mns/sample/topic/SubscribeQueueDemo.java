package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.SubscriptionMeta;
import java.util.Random;

/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 *           mns.accountId=xxxxx
 *           mns.regionId=cn-xxxx
 */
public class SubscribeQueueDemo {
    public static void main(String[] args) {
        String topicName = "TestTopic";
        String subQueueName = "TestQueue";

        String regionId = ServiceSettings.getMNSPropertyValue("regionId","cn-hangzhou");
        String accountId = ServiceSettings.getMNSPropertyValue("accountId","111111111");


        String subArn = String.format("acs:mns:%s:%s:queues/%s", regionId, accountId, subQueueName);

        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();

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
            System.out.println("subscribe/unsubribe error");
        }
        return null;
    }
}
