package com.aliyun.mns.unitTest.model.serialize.queue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.serialize.queue.QueueMetaSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueueMetaSerializerTest {

    private QueueMetaSerializer serializer;

    @Before
    public void setUp() {
        serializer = new QueueMetaSerializer();
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
    public void serialize_QueueMetaWithBasicFields_ShouldSerializeCorrectly() throws Exception {
        QueueMeta meta = new QueueMeta();
        meta.setQueueName("testQueue");
        meta.setPollingWaitSeconds(10);
        meta.setVisibilityTimeout(30L);
        meta.setMaxMessageSize(65536L);
        meta.setDelaySeconds(0L);
        meta.setLoggingEnabled(true);

        InputStream inputStream = serializer.serialize(meta, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<PollingWaitSeconds>10</PollingWaitSeconds>"));
        Assert.assertTrue(xml.contains("<VisibilityTimeout>30</VisibilityTimeout>"));
        Assert.assertTrue(xml.contains("<MaximumMessageSize>65536</MaximumMessageSize>"));
        Assert.assertTrue(xml.contains("<DelaySeconds>0</DelaySeconds>"));
        Assert.assertTrue(xml.contains("<LoggingEnabled>true</LoggingEnabled>"));
    }

}
