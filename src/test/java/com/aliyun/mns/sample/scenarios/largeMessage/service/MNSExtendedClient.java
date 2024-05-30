package com.aliyun.mns.sample.scenarios.largeMessage.service;

import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.TopicMessage;
import java.util.List;

/**
 * 大文件 Client 类
 */
public interface MNSExtendedClient {

    /**
     * Queue 模型
     * 发送消息
     * @param message
     * @return
     */
    Message sendMessage(Message message);

    /**
     * Queue 模型
     * 接受消息, 基于 长轮询实现，样例
     */
    Message receiveMessage(int waitSeconds);

    List<Message> batchReceiveMessage(int batchSize,int waitSeconds);

    /**
     * Queue 模型
     * 删除消息
     */
    void deleteMessage(String receiptHandle);

    /**
     * Topic 模型
     * 发送消息
     */
    TopicMessage publishMessage(TopicMessage msg);
}
