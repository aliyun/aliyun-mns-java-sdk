package com.aliyun.mns.unitTest.model.serialize.topic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.mns.model.MessagePropertyValue;
import com.aliyun.mns.model.MessageSystemPropertyName;
import com.aliyun.mns.model.MessageSystemPropertyValue;
import com.aliyun.mns.model.PropertyType;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.SystemPropertyType;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.request.topic.PublishMessageRequest;
import com.aliyun.mns.model.serialize.topic.TopicMessageSerializer;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

public class TopicMessageSerializerTest {

    private TopicMessageSerializer serializer;

    @Before
    public void setUp() {
        serializer = new TopicMessageSerializer();
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
        TopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("Hello, World!");
        msg.setMessageTag("tag1");

        Map<String, MessagePropertyValue> userProperties = new HashMap<String, MessagePropertyValue>();
        userProperties.put("key1", new MessagePropertyValue(PropertyType.STRING, "value1"));
        userProperties.put("key2", new MessagePropertyValue(PropertyType.BINARY, "value2"));
        msg.setUserProperties(userProperties);

        msg.putSystemProperty(MessageSystemPropertyName.TRACE_PARENT, new MessageSystemPropertyValue(
            SystemPropertyType.STRING, "sysValue1"));

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);

        InputStream inputStream = serializer.serialize(request, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertTrue(xml.contains("<MessageTag>tag1</MessageTag>"));
        Assert.assertTrue(xml.contains("<UserProperties>"));
        Assert.assertTrue(xml.contains("<PropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>key1</Name>"));
        Assert.assertTrue(xml.contains("<Value>value1</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
        Assert.assertTrue(xml.contains("<Name>key2</Name>"));
        Assert.assertTrue(xml.contains(
            String.format("<Value>%s</Value>", new String(Base64.encodeBase64("value2".getBytes(DEFAULT_CHARSET))))));

        Assert.assertTrue(xml.contains("<SystemProperties>"));
        Assert.assertTrue(xml.contains("<SystemPropertyValue>"));
        Assert.assertTrue(xml.contains("<Name>" + MessageSystemPropertyName.TRACE_PARENT.getValue() + "</Name>"));
        Assert.assertTrue(xml.contains("<Value>sysValue1</Value>"));
        Assert.assertTrue(xml.contains("<Type>STRING</Type>"));
    }

    @Test
    public void serialize_MessageWithNullFields_ShouldSerializeCorrectly() throws Exception {
        TopicMessage msg = new RawTopicMessage();
        msg.setUserProperties(null);

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);

        InputStream inputStream = serializer.serialize(request, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody/>"));
        Assert.assertFalse(xml.contains("<UserProperties>"));
        Assert.assertFalse(xml.contains("<SystemProperties>"));
    }

    @Test
    public void serialize_MessageWithEmptyUserProperties_ShouldSerializeCorrectly() throws Exception {
        TopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("Hello, World!");
        msg.setUserProperties(new HashMap<String, MessagePropertyValue>());

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);

        InputStream inputStream = serializer.serialize(request, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertFalse(xml.contains("<UserProperties>"));
    }

    @Test
    public void serialize_MessageWithEmptySystemProperties_ShouldSerializeCorrectly() throws Exception {
        TopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("Hello, World!");

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);

        InputStream inputStream = serializer.serialize(request, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MessageBody>Hello, World!</MessageBody>"));
        Assert.assertFalse(xml.contains("<SystemProperties>"));
    }

}
