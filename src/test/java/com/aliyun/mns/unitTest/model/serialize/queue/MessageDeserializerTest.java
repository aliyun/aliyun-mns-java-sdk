package com.aliyun.mns.unitTest.model.serialize.queue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.MessageSystemPropertyName;
import com.aliyun.mns.model.MessageSystemPropertyValue;
import com.aliyun.mns.model.PropertyType;
import com.aliyun.mns.model.serialize.queue.MessageDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageDeserializerTest {

    private MessageDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new MessageDeserializer();
    }

    @Test
    public void deserialize_MessageIdIsNull_ParseMessage() throws Exception {
        String xml = "<Message><ErrorCode>400</ErrorCode><ErrorMessage>Bad Request</ErrorMessage></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Assert.assertNull(message.getMessageId());
    }

    @Test
    public void deserialize_MessageIdNotNull_ParsesMessageAttributes() throws Exception {
        String xml
            = "<Message><MessageId>12345</MessageId><MessageBody>Hello</MessageBody><ReceiptHandle>handle"
            + "</ReceiptHandle></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertEquals("Hello", message.getMessageBodyAsRawString());
        Assert.assertEquals("handle", message.getReceiptHandle());
    }

    @Test
    public void deserialize_WithUserProperties_ParsesUserProperties() throws Exception {
        String xml
            = "<Message><MessageId>12345</MessageId><UserProperties><PropertyValue><Name>key</Name><Value>value</Value"
            + "><Type>STRING</Type></PropertyValue></UserProperties></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Map<String, MessagePropertyValue> userProperties = message.getUserProperties();
        Assert.assertNotNull(userProperties);
        Assert.assertEquals("value", userProperties.get("key").getStringValue());
    }

    @Test
    public void deserialize_MissingAttributes_HandlesGracefully() throws Exception {
        String xml = "<Message><MessageId>12345</MessageId></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertNull(message.getMessageBody());
        Assert.assertNull(message.getReceiptHandle());
    }

    @Test(expected = Exception.class)
    public void deserialize_MalformedXML_ThrowsException() throws Exception {
        String xml = "<Message><MessageId>12345</MessageId>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        deserializer.deserialize(stream);
    }

    @Test
    public void deserialize_ValidXML_ParsesCorrectly() throws Exception {
        String xml = "<Message>"
            + "<MessageId>12345</MessageId>"
            + "<MessageBody>Test Message</MessageBody>"
            + "<MessageBodyMD5>MD5Hash</MessageBodyMD5>"
            + "<ReceiptHandle>Handle</ReceiptHandle>"
            + "<EnqueueTime>1234567890</EnqueueTime>"
            + "<NextVisibleTime>1234567891</NextVisibleTime>"
            + "<FirstDequeueTime>1234567892</FirstDequeueTime>"
            + "<DequeueCount>1</DequeueCount>"
            + "<Priority>10</Priority>"
            + "<UserProperties>"
            + "<PropertyValue>"
            + "<Name>prop1</Name>"
            + "<Value>value1</Value>"
            + "<Type>STRING</Type>"
            + "</PropertyValue>"
            + "</UserProperties>"
            + "</Message>";

        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Assert.assertEquals("12345", message.getMessageId());
        Assert.assertEquals("Test Message", message.getMessageBodyAsRawString());
        Assert.assertEquals("MD5Hash", message.getMessageBodyMD5());
        Assert.assertEquals("Handle", message.getReceiptHandle());
        Assert.assertEquals(new Date(1234567890L), message.getEnqueueTime());
        Assert.assertEquals(new Date(1234567891L), message.getNextVisibleTime());
        Assert.assertEquals(new Date(1234567892L), message.getFirstDequeueTime());
        Assert.assertEquals(1, message.getDequeueCount().intValue());
        Assert.assertEquals(10, message.getPriority().intValue());

        Map<String, MessagePropertyValue> userProperties = message.getUserProperties();
        Assert.assertNotNull(userProperties);
        Assert.assertEquals(1, userProperties.size());
        MessagePropertyValue propertyValue = userProperties.get("prop1");
        Assert.assertNotNull(propertyValue);
        Assert.assertEquals("value1", propertyValue.getStringValue());
        Assert.assertEquals(PropertyType.STRING, propertyValue.getDataType());
    }

    @Test
    public void deserialize_WithSystemProperties_ParsesSystemProperties() throws Exception {
        String xml
            = "<Message><MessageId>12345</MessageId><SystemProperties><SystemPropertyValue><Name>traceparent</Name"
            + "><Value>sysValue1</Value><Type>STRING</Type></SystemPropertyValue></SystemProperties></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Map<String, MessageSystemPropertyValue> systemProperties = message.getSystemProperties();
        Assert.assertNotNull(systemProperties);
        Assert.assertEquals("sysValue1",
            systemProperties.get(MessageSystemPropertyName.TRACE_PARENT.getValue()).getStringValueByType());
    }

    @Test
    public void deserialize_EmptySystemProperties_ParsesCorrectly() throws Exception {
        String xml
            = "<Message><MessageId>12345</MessageId><SystemProperties></SystemProperties></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Map<String, MessageSystemPropertyValue> systemProperties = message.getSystemProperties();
        Assert.assertTrue(systemProperties.isEmpty());
    }

    @Test
    public void deserialize_MissingSystemProperties_HandlesGracefully() throws Exception {
        String xml
            = "<Message><MessageId>12345</MessageId></Message>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        Message message = deserializer.deserialize(stream);

        Map<String, MessageSystemPropertyValue> systemProperties = message.getSystemProperties();
        Assert.assertTrue(systemProperties.isEmpty());
    }

}
