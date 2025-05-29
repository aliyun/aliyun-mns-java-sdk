package com.aliyun.mns.unitTest.model.serialize.queue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.MessageSystemPropertyName;
import com.aliyun.mns.model.MessageSystemPropertyValue;
import com.aliyun.mns.model.PropertyType;
import com.aliyun.mns.model.SystemPropertyType;
import com.aliyun.mns.model.serialize.queue.MessageSerializer;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

public class MessageSerializerTest {

    private MessageSerializer serializer;

    @Before
    public void setUp() {
        serializer = new MessageSerializer();
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
    public void serialize_MessageWithAllFieldsSet_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");
        msg.setDelaySeconds(10);
        msg.setPriority(5);

        Map<String, MessagePropertyValue> userProperties = new HashMap<String, MessagePropertyValue>();
        userProperties.put("key1", new MessagePropertyValue(PropertyType.STRING, "value1"));
        userProperties.put("key2", new MessagePropertyValue(PropertyType.BINARY, "value2"));
        msg.setUserProperties(userProperties);

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<DelaySeconds>10</DelaySeconds>"));
        Assert.assertTrue(xml.contains("<Priority>5</Priority>"));
        Assert.assertTrue(xml.contains("<UserProperties>"));
        Assert.assertTrue(xml.contains("<PropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>key1</Name>"));
        Assert.assertTrue(xml.contains("<Value>value1</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
        Assert.assertTrue(xml.contains("<Name>key2</Name>"));
        Assert.assertTrue(xml.contains(
            String.format("<Value>%s</Value>", new String(Base64.encodeBase64("value2".getBytes(DEFAULT_CHARSET))))));
    }

    @Test
    public void serialize_MessageWithNullFields_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setUserProperties(null);

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody/>"));
        Assert.assertFalse(xml.contains("<DelaySeconds/>"));
        Assert.assertFalse(xml.contains("<Priority/>"));
        Assert.assertFalse(xml.contains("<UserProperties>"));
    }

    @Test
    public void serialize_MessageWithEmptyUserProperties_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");
        msg.setUserProperties(new HashMap<String, MessagePropertyValue>());

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertFalse(xml.contains("<UserProperties>"));
    }

    @Test
    public void serialize_MessageWithDifferentEncoding_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        InputStream inputStream = serializer.serialize(msg, "UTF-16");
        String xml = convertStreamToString(inputStream, "UTF-16");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
    }

    @Test
    public void serialize_SystemProperties_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        msg.putSystemProperty(MessageSystemPropertyName.TRACE_PARENT, new MessageSystemPropertyValue(
            SystemPropertyType.STRING, "sysValue1"));

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<SystemProperties>"));
        Assert.assertTrue(xml.contains("<SystemPropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>" + MessageSystemPropertyName.TRACE_PARENT.getValue() + "</Name>"));
        Assert.assertTrue(xml.contains("<Value>sysValue1</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
    }

    @Test
    public void serialize_SystemProperties_Empty_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertFalse(xml.contains("<SystemProperties>"));
    }

    @Test
    public void serialize_DLQMessageTypeSystemProperty_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        msg.putSystemProperty(MessageSystemPropertyName.DLQ_MESSAGE_TYPE, new MessageSystemPropertyValue(
            SystemPropertyType.STRING, "test-type"));

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<SystemProperties>"));
        Assert.assertTrue(xml.contains("<SystemPropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>" + MessageSystemPropertyName.DLQ_MESSAGE_TYPE.getValue() + "</Name>"));
        Assert.assertTrue(xml.contains("<Value>test-type</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
    }

    @Test
    public void serialize_DLQSourceARNSystemProperty_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        msg.putSystemProperty(MessageSystemPropertyName.DLQ_SOURCE_ARN, new MessageSystemPropertyValue(
            SystemPropertyType.STRING, "arn:test:source"));

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<SystemProperties>"));
        Assert.assertTrue(xml.contains("<SystemPropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>" + MessageSystemPropertyName.DLQ_SOURCE_ARN.getValue() + "</Name>"));
        Assert.assertTrue(xml.contains("<Value>arn:test:source</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
    }

    @Test
    public void serialize_DLQOriginMessageIDSystemProperty_ShouldSerializeCorrectly() throws Exception {
        Message msg = new Message();
        msg.setMessageBodyAsRawString("Hello, World!");

        msg.putSystemProperty(MessageSystemPropertyName.DLQ_ORIGIN_MESSAGE_ID, new MessageSystemPropertyValue(
            SystemPropertyType.STRING, "origin-msg-123"));

        InputStream inputStream = serializer.serialize(msg, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<SystemProperties>"));
        Assert.assertTrue(xml.contains("<SystemPropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>" + MessageSystemPropertyName.DLQ_ORIGIN_MESSAGE_ID.getValue() + "</Name>"));
        Assert.assertTrue(xml.contains("<Value>origin-msg-123</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
    }

}
