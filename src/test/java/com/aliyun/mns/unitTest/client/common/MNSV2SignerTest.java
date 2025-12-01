package com.aliyun.mns.unitTest.client.common;

import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.MNSV2Signer;
import com.aliyun.mns.common.auth.RequestSigner;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.utils.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MNSV2SignerTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignV2() throws Exception {
        RequestSigner signer = new MNSV2Signer();

        String ak = "ak1";
        String sk = "sk1";
        String region = "region1";
        RequestMessage request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T11:23:48Z"));
        request.setMethod(HttpMethod.POST);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/TestQueue");
        request.addHeader("x-mns-abcDEF", "abcDEF");
        // This header should NOT be included in signature as it doesn't start with x-mns-
        request.addHeader("x-kkk-def", "def");
        String authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS ak1:cAThPWnbphg3hDe3ZHDADTgkgyE=", authorization);

        ak = "ak-kkkk1111111111";
        sk = "sk-kkkkkk1111111111";
        region = "region-kkkkkk1111111111";
        request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-12-03T11:23:48Z"));
        request.setMethod(HttpMethod.GET);
        request.setEndpoint(new URI("https://123456.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/TestQueue123456");
        request.addHeader("X-MNS-abcDEF", "KKKkkk");
        // This header should NOT be included in signature as it doesn't start with x-mns-
        request.addHeader("x-kkk-def", "jjj");

        authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS ak-kkkk1111111111:lPxnfNA+Qo/aj18MYJUnRMqa84I=", authorization);
    }

    /**
     * Test edge cases for MNSV2Signer
     */
    @Test
    public void testSignV2EdgeCases() throws Exception {
        RequestSigner signer = new MNSV2Signer();

        // Test with empty headers
        String ak = "testAk";
        String sk = "testSk";
        String region = "testRegion";
        RequestMessage request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T11:23:48Z"));
        request.setMethod(HttpMethod.GET);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/TestQueue");
        String authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS testAk:uDJQXbk2xOhH95MDXmgbAGdtyE4=", authorization);

        // Test with special characters in resource path
        request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T11:23:48Z"));
        request.setMethod(HttpMethod.POST);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/Test Queue@123");
        request.addHeader("content-type", "application/json");
        authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS testAk:GZ/I9UgZsqvbzfzjd0GmA6VHOx4=", authorization);
    }

    /**
     * Test validation for required parameters
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSignV2MissingAccessKeyId() throws Exception {
        RequestSigner signer = new MNSV2Signer();
        signer.getAuthorization(null, "sk", new RequestMessage(), "region");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSignV2MissingAccessKeySecret() throws Exception {
        RequestSigner signer = new MNSV2Signer();
        signer.getAuthorization("ak", null, new RequestMessage(), "region");
    }

    @Test(expected = NullPointerException.class)
    public void testSignV2MissingRequest() throws Exception {
        RequestSigner signer = new MNSV2Signer();
        signer.getAuthorization("ak", "sk", null, "region");
    }

    @Test(expected = NullPointerException.class)
    public void testSignV2MissingRegion() throws Exception {
        RequestSigner signer = new MNSV2Signer();
        signer.getAuthorization("ak", "sk", new RequestMessage(), null);
    }
}