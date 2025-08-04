package com.aliyun.mns.unitTest.model.serialize.topic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.MessageSystemPropertyName;
import com.aliyun.mns.model.MessageSystemPropertyValue;
import com.aliyun.mns.model.PropertyType;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.serialize.topic.TopicMessageDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicMessageDeserializerTest {

    private TopicMessageDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new TopicMessageDeserializer(TopicMessage.BodyType.STRING);
    }

    @Test
    public void deserialize_MessageIdIsNull_ParseMessage() throws Exception {
        String xml = "<Message><ErrorCode>400</ErrorCode><ErrorMessage>Bad Request</ErrorMessage></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        TopicMessage message = deserializer.deserialize(stream);

        Assert.assertNull(message.getMessageId());
    }

    @Test
    public void deserialize_WithMessageGroupId_ParsesMessageGroupId() throws Exception {
        String xml = "<Message>"
            + "<MessageId>12345</MessageId>"
            + "<MessageBody>Test Message</MessageBody>"
            + "<MessageTag>tag1</MessageTag>"
            + "<MessageGroupId>test-topic-group-id</MessageGroupId>"
            + "</Message>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        TopicMessage message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertEquals("test-topic-group-id", message.getMessageGroupId());
    }

    @Test
    public void deserialize_WithEmptyMessageGroupId_ParsesCorrectly() throws Exception {
        String xml = "<Message>"
            + "<MessageId>12345</MessageId>"
            + "<MessageBody>Test Message</MessageBody>"
            + "<MessageGroupId></MessageGroupId>"
            + "</Message>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        TopicMessage message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertEquals("", message.getMessageGroupId());
    }

    @Test
    public void deserialize_WithoutMessageGroupId_ReturnsNull() throws Exception {
        String xml = "<Message>"
            + "<MessageId>12345</MessageId>"
            + "<MessageBody>Test Message</MessageBody>"
            + "</Message>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        TopicMessage message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertNull(message.getMessageGroupId());
    }
}
