package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;

public class DeleteTopicDemo {
    public static void main(String[] args) {
        String topicName = "TestTopic";


        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        //this client need only initialize once
        MNSClient client = account.getMNSClient();

        CloudTopic topic = client.getTopicRef(topicName);
        try {
            topic.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("delete topic error");
        }

        client.close();
    }
}
