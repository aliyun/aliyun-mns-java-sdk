package com.aliyun.mns.sample.scenarios.largeMessage.service;

import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.ServiceHandlingRequiredException;
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
    Message sendMessage(Message message) throws ServiceException;

    /**
     * Queue 模型
     * 接受消息, 基于 长轮询实现，样例
     */
    Message receiveMessage(int waitSeconds) throws  ServiceHandlingRequiredException;

    List<Message> batchReceiveMessage(int batchSize,int waitSeconds)
        throws  ServiceHandlingRequiredException;

    /**
     * Queue 模型
     * 删除消息
     */
    void deleteMessage(String receiptHandle) throws ServiceHandlingRequiredException;

    /**
     * Topic 模型
     * 发送消息
     */
    TopicMessage publishMessage(TopicMessage msg) throws ServiceException;
}
