package com.aliyun.mns.unitTest.model.serialize.queue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.serialize.queue.MessageListSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageListSerializerTest {

    private MessageListSerializer serializer;

    @Before
    public void setUp() {
        serializer = new MessageListSerializer();
    }

    private String convertStreamToString(InputStream inputStream, String encoding) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(encoding);
    }

    @Test
    public void serialize_NullMessagesList_ReturnsEmptyXml() throws Exception {
        InputStream inputStream = serializer.serialize(null, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");
        Assert.assertTrue(xml.contains("<Messages xmlns=\"http://mns.aliyuncs.com/doc/v1\"/>"));
    }

    @Test
    public void serialize_EmptyMessagesList_ReturnsEmptyXml() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        InputStream inputStream = serializer.serialize(messages, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");
        Assert.assertTrue(xml.contains("<Messages xmlns=\"http://mns.aliyuncs.com/doc/v1\"/>"));
    }

    @Test
    public void serialize_SingleMessage_ReturnsXml() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message();
        message.setMessageBodyAsRawString("Test Message");
        message.setDelaySeconds(10);
        message.setPriority(1);
        messages.add(message);

        InputStream inputStream = serializer.serialize(messages, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");
        Assert.assertTrue(xml.contains("<MessageBody>Test Message</MessageBody>"));
        Assert.assertTrue(xml.contains("<DelaySeconds>10</DelaySeconds>"));
        Assert.assertTrue(xml.contains("<Priority>1</Priority>"));
    }

    @Test
    public void serialize_MultipleMessages_ReturnsXml() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        Message message1 = new Message();
        message1.setMessageBodyAsRawString("Message 1");
        message1.setDelaySeconds(5);
        message1.setPriority(2);
        messages.add(message1);

        Message message2 = new Message();
        message2.setMessageBodyAsRawString("Message 2");
        message2.setDelaySeconds(15);
        message2.setPriority(3);
        messages.add(message2);

        InputStream inputStream = serializer.serialize(messages, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");
        Assert.assertTrue(xml.contains("<MessageBody>Message 1</MessageBody>"));
        Assert.assertTrue(xml.contains("<MessageBody>Message 2</MessageBody>"));
    }

    @Test
    public void serialize_MessageWithUserProperties_ReturnsXml() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message();
        message.setMessageBodyAsRawString("Test Message");
        Map<String, MessagePropertyValue> userProperties = new HashMap<String, MessagePropertyValue>();
        userProperties.put("key1", new MessagePropertyValue("value1"));
        message.setUserProperties(userProperties);
        messages.add(message);

        InputStream inputStream = serializer.serialize(messages, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");
        Assert.assertTrue(xml.contains("<MessageBody>Test Message</MessageBody>"));
        Assert.assertTrue(xml.contains(
            "<UserProperties><PropertyValue><Name>key1</Name><Value>value1</Value><Type>STRING</Type></PropertyValue"
                + "></UserProperties>"));
    }

    @Test
    public void serialize_InvalidEncoding_ThrowsException() {
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message("Test Message");
        messages.add(message);

        try {
            serializer.serialize(messages, "INVALID_ENCODING");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("INVALID_ENCODING"));
        }
    }
}
