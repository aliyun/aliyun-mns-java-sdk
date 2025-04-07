package com.aliyun.mns.unitTest.model.serialize.queue;


import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.serialize.queue.MessageListDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MessageListDeserializerTest {

    private MessageListDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new MessageListDeserializer();
    }

    @Test
    public void deserialize_EmptyInputStream_ReturnsEmptyList() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("<Messages/>".getBytes());
        List<Message> messages = deserializer.deserialize(inputStream);
        Assert.assertTrue(messages == null || messages.isEmpty());
    }

    @Test(expected = Exception.class)
    public void deserialize_MalformedXML_ThrowsException() throws Exception {
        String malformedXml = "<Messages><Message><MessageId>123</MessageId></Message>";
        InputStream inputStream = new ByteArrayInputStream(malformedXml.getBytes());
        deserializer.deserialize(inputStream);
    }

    @Test
    public void deserialize_ValidXML_ReturnsMessages() throws Exception {
        String validXml = "<Messages><Message><MessageId>123</MessageId><MessageBody>Test Message</MessageBody></Message></Messages>";
        InputStream inputStream = new ByteArrayInputStream(validXml.getBytes());
        List<Message> messages = deserializer.deserialize(inputStream);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("123", messages.get(0).getMessageId());
        Assert.assertEquals("Test Message", messages.get(0).getMessageBodyAsRawString());
    }

    @Test
    public void deserialize_XMLWithEmptyMessages_ReturnsEmptyMessages() throws Exception {
        String xmlWithEmptyMessages = "<Messages><Message><MessageId></MessageId><MessageBody></MessageBody></Message></Messages>";
        InputStream inputStream = new ByteArrayInputStream(xmlWithEmptyMessages.getBytes());
        List<Message> messages = deserializer.deserialize(inputStream);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("", messages.get(0).getMessageId());
        Assert.assertEquals("", messages.get(0).getMessageBody());
    }
}
