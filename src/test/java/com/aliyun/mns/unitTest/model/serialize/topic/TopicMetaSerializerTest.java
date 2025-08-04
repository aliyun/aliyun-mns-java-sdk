package com.aliyun.mns.unitTest.model.serialize.topic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.serialize.topic.TopicMetaSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicMetaSerializerTest {

    private TopicMetaSerializer serializer;

    @Before
    public void setUp() {
        serializer = new TopicMetaSerializer();
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
    public void serialize_TopicMetaWithBasicFields_ShouldSerializeCorrectly() throws Exception {
        TopicMeta meta = new TopicMeta();
        meta.setTopicName("testTopic");
        meta.setMaxMessageSize(65536L);
        meta.setLoggingEnabled(true);

        InputStream inputStream = serializer.serialize(meta, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<MaximumMessageSize>65536</MaximumMessageSize>"));
        Assert.assertTrue(xml.contains("<LoggingEnabled>true</LoggingEnabled>"));
    }
}
