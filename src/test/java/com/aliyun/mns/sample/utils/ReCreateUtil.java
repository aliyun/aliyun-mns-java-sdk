package com.aliyun.mns.sample.utils;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ThreadUtil;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.TopicMeta;

/**
 * @author zxg
 * @date 2024/05/21
 * @Description if it has bugs.please forgive me ~~~
 * @Description 待添加详细描述
 */
public class ReCreateUtil {

    public static void reCreateQueue(MNSClient client, String queueName) {
        CloudQueue queue = client.getQueueRef(queueName);
        queue.delete();

        QueueMeta meta = new QueueMeta();
        meta.setQueueName(queueName);
        client.createQueue(meta);

        // make sure queue exist
        ThreadUtil.sleep(200L);
    }

    public static void reCreateTopic(MNSClient client,String topicName) {
        CloudTopic topic = client.getTopicRef(topicName);
        topic.delete();

        TopicMeta meta = new TopicMeta();
        meta.setTopicName(topicName);
        client.createTopic(meta);

        // make sure queue exist
        ThreadUtil.sleep(200L);
    }
}
