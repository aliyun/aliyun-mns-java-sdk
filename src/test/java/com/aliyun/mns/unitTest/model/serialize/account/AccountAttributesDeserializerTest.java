package com.aliyun.mns.unitTest.model.serialize.account;

import com.aliyun.mns.model.AccountAttributes;
import com.aliyun.mns.model.serialize.account.AccountAttributesDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.aliyun.mns.common.MNSConstants.LOGGING_BUCKET_TAG;
import static com.aliyun.mns.common.MNSConstants.TRACE_ENABLED_TAG;

public class AccountAttributesDeserializerTest {

    private AccountAttributesDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new AccountAttributesDeserializer();
    }

    @Test
    public void deserialize_WithAllFieldsSet_ReturnsCorrectAttributes() throws Exception {
        String xml = String.format(
            "<Account xmlns=\"%s\">" +
                "<%s>test-bucket</%s>" +
                "<%s>1</%s>" +
                "</Account>",
            "http://mns.aliyuncs.com/doc/v1", // 假设命名空间与代码中的 DEFAULT_XML_NAMESPACE 一致
            LOGGING_BUCKET_TAG, LOGGING_BUCKET_TAG,
            TRACE_ENABLED_TAG, TRACE_ENABLED_TAG
        );
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        AccountAttributes account = deserializer.deserialize(stream);

        Assert.assertEquals("test-bucket", account.getLoggingBucket());
        Assert.assertEquals(1, account.getTraceEnabled().intValue());
    }

    @Test
    public void deserialize_WithoutLoggingBucket_ReturnsNull() throws Exception {
        String xml = String.format(
            "<Account xmlns=\"%s\">" +
                "<%s>0</%s>" +
                "</Account>",
            "http://mns.aliyuncs.com/doc/v1",
            TRACE_ENABLED_TAG, TRACE_ENABLED_TAG
        );
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        AccountAttributes account = deserializer.deserialize(stream);

        Assert.assertNull(account.getLoggingBucket());
        Assert.assertEquals(0, account.getTraceEnabled().intValue());
    }

    @Test
    public void deserialize_WithoutTraceEnabled_ReturnsNull() throws Exception {
        String xml = String.format(
            "<Account xmlns=\"%s\">" +
                "<%s>test-bucket</%s>" +
                "</Account>",
            "http://mns.aliyuncs.com/doc/v1",
            LOGGING_BUCKET_TAG, LOGGING_BUCKET_TAG
        );
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        AccountAttributes account = deserializer.deserialize(stream);

        Assert.assertEquals("test-bucket", account.getLoggingBucket());
        Assert.assertNull(account.getTraceEnabled());
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_InvalidTraceEnabled_ThrowsException() throws Exception {
        String xml = String.format(
            "<Account xmlns=\"%s\">" +
                "<%s>invalid</%s>" +
                "</Account>",
            "http://mns.aliyuncs.com/doc/v1",
            TRACE_ENABLED_TAG, TRACE_ENABLED_TAG
        );
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        deserializer.deserialize(stream);
    }

    @Test
    public void deserialize_EmptyXml_ReturnsDefaults() throws Exception {
        String xml = "<Account/>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        AccountAttributes account = deserializer.deserialize(stream);

        Assert.assertNull(account.getLoggingBucket());
        Assert.assertNull(account.getTraceEnabled());
    }

    @Test(expected = Exception.class)
    public void deserialize_MalformedXml_ThrowsException() throws Exception {
        String xml = "<Account><LoggingBucket>test</LoggingBucket>"; // 未闭合的标签
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        deserializer.deserialize(stream);
    }

}
