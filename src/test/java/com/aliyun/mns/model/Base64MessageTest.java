package com.aliyun.mns.model;

import java.util.Arrays;
import java.util.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description Base64 message 单测
 */
public class Base64MessageTest {

    @Test
    public void queueSetMessageWithoutBase64() {
        String testString = "Hello, World!";
        byte[] testBytes = testString.getBytes();

        Message message = new Message();
        // 设置消息体
        message.setMessageBodyAsRawString(testBytes);

        // 验证消息体是否正确设置, body 值为 原始值
        Assert.assertArrayEquals(testBytes, message.getMessageBodyBytes());
        Assert.assertArrayEquals(testBytes, message.getMessageBodyAsRawBytes());
    }

    @Test
    public void queueSetMessageWithBase64() {
        String testString = "Hello, World!";
        byte[] testBytes = testString.getBytes();

        Message message = new Message();
        // 设置消息体
        message.setMessageBody(testBytes);

        // 验证消息体是否正确设置, body 值 非原始值
        Assert.assertFalse(Arrays.equals(testBytes, message.getMessageBodyBytes()));
        // body 值 为 base 64 后 值
        Assert.assertArrayEquals(Base64.getEncoder().encode(testBytes), message.getMessageBodyBytes());
    }


    @Test
    public void topicSetMessageWithoutBase64() {
        String testString = "Hello, World!";
        byte[] testBytes = testString.getBytes();

        TopicMessage message = new RawTopicMessage();
        // 设置消息体
        message.setMessageBody(testBytes);

        // 验证消息体是否正确设置, body 值为 原始值
        Assert.assertArrayEquals(testBytes, message.getMessageBodyBytes());
        Assert.assertEquals(testString, message.getMessageBody());
    }

    @Test
    public void topicSetMessageWithBase64() {
        String testString = "Hello, World!";
        byte[] testBytes = testString.getBytes();

        TopicMessage message = new Base64TopicMessage();
        // 设置消息体
        message.setMessageBody(testBytes);

        // 验证消息体是否正确设置, body 值 非原始值
        Assert.assertFalse(Arrays.equals(testBytes, message.getMessageBodyBytes()));

        // body 值 为 base 64 后 值
        Assert.assertArrayEquals(Base64.getEncoder().encode(testBytes), message.getMessageBodyBytes());

        // get 也 做过解码，因此匹配
        Assert.assertEquals(testString, message.getMessageBody());
    }
}
