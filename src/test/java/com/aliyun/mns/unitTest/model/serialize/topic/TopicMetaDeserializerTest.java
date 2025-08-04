package com.aliyun.mns.unitTest.model.serialize.topic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.serialize.topic.TopicMetaDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicMetaDeserializerTest {

    private TopicMetaDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new TopicMetaDeserializer();
    }

    @Test
    public void deserialize_TopicMetaWithBasicFields_ShouldDeserializeCorrectly() throws Exception {
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<Topic xmlns=\"http://mns.aliyuncs.com/doc/v1/\">" +
                "<TopicName>testTopic</TopicName>" +
                "<CreateTime>1234567890</CreateTime>" +
                "<LastModifyTime>1234567891</LastModifyTime>" +
                "<MaximumMessageSize>65536</MaximumMessageSize>" +
                "<MessageRetentionPeriod>345600</MessageRetentionPeriod>" +
                "<MessageCount>0</MessageCount>" +
                "<LoggingEnabled>true</LoggingEnabled>" +
            "</Topic>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        TopicMeta meta = deserializer.deserialize(stream);

        Assert.assertEquals("testTopic", meta.getTopicName());
        Assert.assertEquals(65536, meta.getMaxMessageSize().longValue());
        Assert.assertEquals(345600, meta.getMessageRetentionPeriod().longValue());
        Assert.assertEquals(0, meta.getMessageCount().longValue());
        Assert.assertEquals(true, meta.isLoggingEnabled());
    }
}
