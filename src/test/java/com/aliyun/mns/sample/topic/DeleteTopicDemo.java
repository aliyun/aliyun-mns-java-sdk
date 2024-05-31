package com.aliyun.mns.sample.topic;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;
/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.endpoint=http://xxxxxxx
 */
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
