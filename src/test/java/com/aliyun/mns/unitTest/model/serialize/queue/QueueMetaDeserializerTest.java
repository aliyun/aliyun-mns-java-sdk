package com.aliyun.mns.unitTest.model.serialize.queue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.serialize.queue.QueueMetaDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueueMetaDeserializerTest {

    private QueueMetaDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new QueueMetaDeserializer();
    }

    @Test
    public void deserialize_QueueMetaWithBasicFields_ShouldDeserializeCorrectly() throws Exception {
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<Queue xmlns=\"http://mns.aliyuncs.com/doc/v1/\">" +
                "<QueueName>testQueue</QueueName>" +
                "<CreateTime>1234567890</CreateTime>" +
                "<LastModifyTime>1234567891</LastModifyTime>" +
                "<VisibilityTimeout>30</VisibilityTimeout>" +
                "<DelaySeconds>0</DelaySeconds>" +
                "<MaximumMessageSize>65536</MaximumMessageSize>" +
                "<MessageRetentionPeriod>345600</MessageRetentionPeriod>" +
                "<PollingWaitSeconds>10</PollingWaitSeconds>" +
                "<ActiveMessages>0</ActiveMessages>" +
                "<InactiveMessages>0</InactiveMessages>" +
                "<DelayMessages>0</DelayMessages>" +
                "<LoggingEnabled>true</LoggingEnabled>" +
            "</Queue>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        QueueMeta meta = deserializer.deserialize(stream);

        Assert.assertEquals("testQueue", meta.getQueueName());
        Assert.assertEquals(30, meta.getVisibilityTimeout().longValue());
        Assert.assertEquals(0, meta.getDelaySeconds().longValue());
        Assert.assertEquals(65536, meta.getMaxMessageSize().longValue());
        Assert.assertEquals(345600, meta.getMessageRetentionPeriod().longValue());
        Assert.assertEquals(10, meta.getPollingWaitSeconds().intValue());
        Assert.assertEquals(0, meta.getActiveMessages().longValue());
        Assert.assertEquals(0, meta.getInactiveMessages().longValue());
        Assert.assertEquals(0, meta.getDelayMessages().longValue());
        Assert.assertEquals(true, meta.isLoggingEnabled());
    }

}
