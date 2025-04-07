package com.aliyun.mns.unitTest.model.serialize.account;

import com.aliyun.mns.model.AccountAttributes;
import com.aliyun.mns.model.serialize.account.AccountAttributesSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.aliyun.mns.common.MNSConstants.LOGGING_BUCKET_TAG;
import static com.aliyun.mns.common.MNSConstants.TRACE_ENABLED_TAG;

public class AccountAttributesSerializerTest {

    private AccountAttributesSerializer serializer;

    @Before
    public void setUp() {
        serializer = new AccountAttributesSerializer();
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
    public void serialize_WithAllFieldsSet_ShouldIncludeBothElements() throws Exception {
        AccountAttributes account = new AccountAttributes();
        account.setLoggingBucket("test-bucket");
        account.setTraceEnabled(1);

        InputStream inputStream = serializer.serialize(account, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<" + LOGGING_BUCKET_TAG + ">test-bucket</" + LOGGING_BUCKET_TAG + ">"));
        Assert.assertTrue(xml.contains("<" + TRACE_ENABLED_TAG + ">1</" + TRACE_ENABLED_TAG + ">"));
    }

    @Test
    public void serialize_WithNullLoggingBucket_ShouldOmitLoggingElement() throws Exception {
        AccountAttributes account = new AccountAttributes();
        account.setLoggingBucket(null);
        account.setTraceEnabled(0);

        InputStream inputStream = serializer.serialize(account, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertFalse(xml.contains(LOGGING_BUCKET_TAG));
        Assert.assertTrue(xml.contains("<" + TRACE_ENABLED_TAG + ">0</" + TRACE_ENABLED_TAG + ">"));
    }

    @Test
    public void serialize_WithNullTraceEnabled_ShouldOmitTraceElement() throws Exception {
        AccountAttributes account = new AccountAttributes();
        account.setLoggingBucket("test-bucket");
        account.setTraceEnabled(null);

        InputStream inputStream = serializer.serialize(account, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains(LOGGING_BUCKET_TAG));
        Assert.assertFalse(xml.contains(TRACE_ENABLED_TAG));
    }

    @Test
    public void serialize_WithAllNull_ShouldHaveEmptyRoot() throws Exception {
        AccountAttributes account = new AccountAttributes();
        account.setLoggingBucket(null);
        account.setTraceEnabled(null);

        InputStream inputStream = serializer.serialize(account, "UTF-8");
        String xml = convertStreamToString(inputStream, "UTF-8");

        Assert.assertTrue(xml.contains("<Account xmlns=\"http://mns.aliyuncs.com/doc/v1\"/>"));
    }

    @Test
    public void serialize_WithDifferentEncoding_ShouldWork() throws Exception {
        AccountAttributes account = new AccountAttributes();
        account.setLoggingBucket("test-bucket");

        InputStream inputStream = serializer.serialize(account, "UTF-16");
        String xml = convertStreamToString(inputStream, "UTF-16");

        Assert.assertTrue(xml.contains(LOGGING_BUCKET_TAG));
    }
}
