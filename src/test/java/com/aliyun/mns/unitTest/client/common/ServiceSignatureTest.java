package com.aliyun.mns.unitTest.client.common;

import com.aliyun.mns.common.auth.HmacSHA1Signature;
import com.aliyun.mns.common.auth.HmacSHA256Signature;
import com.aliyun.mns.common.auth.ServiceSignature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class ServiceSignatureTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHashWithHmacSHA1() {
        ServiceSignature serviceSignature = new HmacSHA1Signature();

        String contentToSign = "contentToSign111 Hello world";
        String signature = serviceSignature.computeHashAndBase64Encode("accessKeySecret111", contentToSign);
        Assert.assertEquals("ZX9CiXzbkdkW2bW1Lw3G3JvnD90=", signature);

        contentToSign = "PUT\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Tue, 03 Jun 2025 11:23:48 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/TestQueue";
        signature = serviceSignature.computeHashAndBase64Encode("accessKeySecret111", contentToSign);
        Assert.assertEquals("HCSlBi5KuXGNf06tZKFiQlNDt4c=", signature);

        contentToSign = "PUT\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Tue, 03 Jun 2025 11:23:48 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/TestQueue";
        signature = serviceSignature.computeHashAndBase64Encode("skkkkkkk1111111111", contentToSign);
        Assert.assertEquals("KvUgeFIzq+V37Fw7Nb8BKQEncZU=", signature);
    }

    @Test
    public void testHashWithHmacSHA256() {
        ServiceSignature serviceSignature = new HmacSHA256Signature();

        String contentToSign = "contentToSign111 Hello world";
        String signature = serviceSignature.computeHashAndBase64Encode("accessKeySecret111", contentToSign);
        Assert.assertEquals("L8DbTo8IRKuE+n9SPCWt0bzQ6Ahy9MQOzBaAsOoT4go=", signature);

        contentToSign = "PUT\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Tue, 03 Jun 2025 11:23:48 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/TestQueue";
        signature = serviceSignature.computeHashAndBase64Encode("accessKeySecret111", contentToSign);
        Assert.assertEquals("gG99bIzy+U231f6kE51jYqwJWTtcvHQ2ajThiyjMD3s=", signature);

        contentToSign = "PUT\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Tue, 03 Jun 2025 11:23:48 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/TestQueue";
        signature = serviceSignature.computeHashAndBase64Encode("skkkkkkk1111111111", contentToSign);
        Assert.assertEquals("1KD0l2/8M/42vXZfTf4LUY1gW3cxCCubw9zktiHx8GE=", signature);
    }
}