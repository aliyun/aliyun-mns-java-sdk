package com.aliyun.mns.unitTest.client.common;

import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.auth.MNSV4Signer;
import com.aliyun.mns.common.auth.RequestSigner;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.utils.DateUtil;
import com.aliyun.mns.common.utils.HttpHeaders;
import com.aliyun.mns.common.utils.IdptEnvUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MNSV4SignerTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testV4BuildSignature() throws Exception {
        MNSV4Signer signer = new MNSV4Signer();
        String v4SigningKeyBase64 = "biPl5icbr+5R2EFnPBDy0XkgiSRZqIQlBNi3PNhsfpg=";
        String contentToSign = "GET\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Wed, 04 Jun 2025 06:33:37 GMT\n"
            + "x-mns-ret-number:1\n"
            + "x-mns-version:2015-06-06\n"
            + "x-mns-with-meta:false\n"
            + "/queues";
        String v4Signature = signer.buildSignature(Base64.decodeBase64(v4SigningKeyBase64), contentToSign);
        Assert.assertEquals("da2ee97bef2e4e7a0a3c6e98b501963b2502b67218ea712de4e8d3c7e047a83f", v4Signature);

        v4SigningKeyBase64 = "biPl5icbr+5R2EFnPBDy0XkgiSRZqIQlBNi3PNhsfpg=";
        contentToSign = "PUT\n"
            + "\n"
            + "text/xml;charset=UTF-8\n"
            + "Tue, 03 Jun 2025 11:23:48 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/TestQueue";
        v4Signature = signer.buildSignature(Base64.decodeBase64(v4SigningKeyBase64), contentToSign);
        Assert.assertEquals("b5e06cd0cba5fbab4fff9e33bb654dbae64bdb90421d965dcf3c46889fd8ad60", v4Signature);

        v4SigningKeyBase64 = "2HhAzNXcwZJs3dhqkYg7yqkUjS3so+UdhbUR+eKEgtQ=";
        contentToSign = "MNS4-HMAC-SHA256\n"
            + "20251124T091359Z\n"
            + "20251124/eu-west-1/mns/aliyun_v4_request\n"
            + "PUT\n"
            + "/queues/queue-auto-25112417-l0PHHVTHYv\n"
            + "\n"
            + "content-type:text/xml;charset=UTF-8\n"
            + "x-mns-version:2015-06-06\n"
            + "\n";
        v4Signature = signer.buildSignature(Base64.decodeBase64(v4SigningKeyBase64), contentToSign);
        Assert.assertEquals("63ebcf43397e672c47d48ca5d9ef7245e59626da7a3d58d37c75cde993429e98", v4Signature);
    }

    /**
     * Test edge cases for V4SignHelper
     */
    @Test
    public void testV4BuildSignatureEdgeCases() throws Exception {
        MNSV4Signer signer = new MNSV4Signer();

        // Test with minimal content
        String v4SigningKeyBase64 = "testKeyBase64=";
        String contentToSign = "GET\n"
            + "\n"
            + "\n"
            + "Wed, 04 Jun 2025 06:33:37 GMT\n"
            + "/queues";
        String v4Signature = signer.buildSignature(Base64.decodeBase64(v4SigningKeyBase64), contentToSign);
        Assert.assertEquals("0f05d1c75194465126ded4c71030eba629164b2a4ec919eebedc515506aac70c", v4Signature);

        // Test with special characters in resource path
        v4SigningKeyBase64 = "anotherTestKeyBase64=";
        contentToSign = "POST\n"
            + "\n"
            + "application/json\n"
            + "Wed, 04 Jun 2025 06:33:37 GMT\n"
            + "x-mns-version:2015-06-06\n"
            + "/queues/Test Queue@123";
        v4Signature = signer.buildSignature(Base64.decodeBase64(v4SigningKeyBase64), contentToSign);
        Assert.assertEquals("7b54f1ca5a071b9ad9b6c2aec36a75b3813a76887c76d9eae980d909230506ec", v4Signature);
    }

    /**
     * 公有云环境V4签名测试
     */
    @Test
    public void testSignV4PublicCloud() throws Exception {
        RequestSigner signer = new MNSV4Signer();

        // case1: QueryString
        String ak = "ak1";
        String sk = "sk1";
        String region = "region1";
        RequestMessage request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T10:52:35.638Z"));
        request.setMethod(HttpMethod.POST);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/TestQueue/");
        request.addParameter("param1", "abc");
        request.addParameter("PARAM2", "d  ef");
        request.addParameter("PARAM3", "[def]#bbb");
        request.addParameter("PARAM4", "中文参数");
        String authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=ak1/20250603/region1/mns/aliyun_v4_request,Signature=1fc6013f158d4c3aba09570895a2a93c8445c2c5213d82a1463baf602cf0d945", authorization);

        // case2: custom Header
        ak = "akkkkk1111111111";
        sk = "skkkkkkk1111111111";
        region = "region2";
        request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-12-03T11:23:48Z"));
        request.setMethod(HttpMethod.PUT);
        request.setEndpoint(new URI("https://123456.mns.cn-hangzhou.aliyuncs.com"));
        // resourcePath is not start with '/'
        request.setResourcePath("queues/TestQueue123456");
        request.addHeader("x-mns-abc", "kkk");
        request.addHeader("X-MNS-Sss", "2015-06-06xFFF");
        request.addHeader("x-kkk-def", "jjj");
        request.addHeader("X-KKK-def", "lll");
        authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=akkkkk1111111111/20251203/region2/mns/aliyun_v4_request,Signature=04f1e5f929b5aaac3af6a54970f591581ecd2aade2562f926c5f2ec5d5c8cfad",
            authorization);

        // case3: default header
        ak = "akkkkk222222222";
        sk = "skkkkkk2222222222";
        region = "eu-west-1";
        request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-12-04T06:32:35.638Z"));
        request.setMethod(HttpMethod.GET);
        request.setEndpoint(new URI("https://789.mns.eu-west-1.aliyuncs.com"));
        request.setResourcePath("/queues");
        request.addHeader("x-mns-version", "2015-06-30");
        request.addHeader("x-mns-ttt", "1");
        request.addHeader("X-MNS-UUUuu", "FALse");
        request.addHeader("bb-MNS-UUUuu", "abcdddDDD");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.CONTENT_MD5, "0d9d2ce750000593eb34f00");
        authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=akkkkk222222222/20251204/eu-west-1/mns/aliyun_v4_request,Signature=294f776a2d5726df679b54b081ab8126a199a2545c8c8a7a875adff0b386e458",
            authorization);
    }

    /**
     * IDPT环境V4签名测试
     */
    @Test
    public void testSignV4IdptCloud() throws Exception {
        try (MockedStatic<IdptEnvUtil> mockedIdptEnvUtil = mockStatic(IdptEnvUtil.class)) {
            mockedIdptEnvUtil.when(IdptEnvUtil::isIdptEnv).thenReturn(true);

            RequestSigner signer = new MNSV4Signer();

            // case1: QueryString
            String ak = "ak1";
            String sk = "sk1";
            String region = "region1";
            RequestMessage request = spy(new RequestMessage());
            when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T10:52:35.638Z"));
            request.setMethod(HttpMethod.POST);
            request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
            request.setResourcePath("/queues/TestQueue");
            request.addParameter("param1", "abc");
            request.addParameter("PARAM2", "d  ef");
            request.addParameter("PARAM3", "[def]#bbb");
            request.addParameter("PARAM4", "中文参数");
            String authorization = signer.getAuthorization(ak, sk, request, region);
            Assert.assertEquals("APSARA-MNS4-HMAC-SHA256 Credential=ak1/20250603/region1/mns/apsara_v4_request,Signature=51269315dedfd0f0252cbca05356c85f5005077796a1e8463c54d6716f331389",
                authorization);

            // case2: custom Header
            ak = "akkkkk1111111111";
            sk = "skkkkkkk1111111111";
            region = "region2";
            request = spy(new RequestMessage());
            when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-12-03T11:23:48Z"));
            request.setMethod(HttpMethod.PUT);
            request.setEndpoint(new URI("https://123456.mns.cn-hangzhou.aliyuncs.com"));
            request.setResourcePath("/queues/TestQueue123456");
            request.addHeader("x-mns-abc", "kkk");
            request.addHeader("X-MNS-Sss", "2015-06-06xFFF");
            request.addHeader("x-kkk-def", "jjj");
            request.addHeader("X-KKK-def", "lll");
            authorization = signer.getAuthorization(ak, sk, request, region);
            Assert.assertEquals("APSARA-MNS4-HMAC-SHA256 Credential=akkkkk1111111111/20251203/region2/mns/apsara_v4_request,Signature=5bddaa9d4931254d477cc601836e6c2477bb793c499ac3d17a9493cf98786c51",
                authorization);

            // case3: default header
            ak = "akkkkk222222222";
            sk = "skkkkkk2222222222";
            region = "eu-west-1";
            request = spy(new RequestMessage());
            when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-12-04T06:32:35.638Z"));
            request.setMethod(HttpMethod.GET);
            request.setEndpoint(new URI("https://789.mns.eu-west-1.aliyuncs.com"));
            request.setResourcePath("/queues");
            request.addHeader("x-mns-version", "2015-06-30");
            request.addHeader("x-mns-ttt", "1");
            request.addHeader("X-MNS-UUUuu", "FALse");
            request.addHeader("bb-MNS-UUUuu", "abcdddDDD");
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.addHeader(HttpHeaders.CONTENT_MD5, "0d9d2ce750000593eb34f00");
            authorization = signer.getAuthorization(ak, sk, request, region);
            Assert.assertEquals(
                "APSARA-MNS4-HMAC-SHA256 Credential=akkkkk222222222/20251204/eu-west-1/mns/apsara_v4_request,Signature=b0f49b3575379975f04f728ba9f6771dfd451c226d16924d42938953396c9e8f",
                authorization);
        }
    }

    /**
     * Test edge cases for MNSV4Signer
     */
    @Test
    public void testSignV4EdgeCases() throws Exception {
        RequestSigner signer = new MNSV4Signer();

        // Test with minimal request
        String ak = "testAk";
        String sk = "testSk";
        String region = "testRegion";
        RequestMessage request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T10:52:35.638Z"));
        request.setMethod(HttpMethod.GET);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/");
        String authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=testAk/20250603/testRegion/mns/aliyun_v4_request,Signature=63f78964dcd1132bab91b9c38ceebdfb59509149b34874c5b2c9dfc2b12e7291", authorization);

        // Test with special characters in resource path
        request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T10:52:35.638Z"));
        request.setMethod(HttpMethod.POST);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/Test Queue@123");
        authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=testAk/20250603/testRegion/mns/aliyun_v4_request,Signature=87de7499365a31e767cdc0c726a893c72c860e415876f7f92914197ac3795833", authorization);
    }

    /**
     * Test validation for required parameters
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSignV4MissingAccessKeyId() throws Exception {
        RequestSigner signer = new MNSV4Signer();
        signer.getAuthorization(null, "sk", new RequestMessage(), "region");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSignV4MissingAccessKeySecret() throws Exception {
        RequestSigner signer = new MNSV4Signer();
        signer.getAuthorization("ak", null, new RequestMessage(), "region");
    }

    @Test(expected = NullPointerException.class)
    public void testSignV4MissingRequest() throws Exception {
        RequestSigner signer = new MNSV4Signer();
        signer.getAuthorization("ak", "sk", null, "region");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSignV4MissingRegion() throws Exception {
        RequestSigner signer = new MNSV4Signer();
        signer.getAuthorization("ak", "sk", new RequestMessage(), null);
    }

    @Test
    public void testSignV4WithResourcePathContainsParam() throws Exception {
        RequestSigner signer = new MNSV4Signer();

        String ak = "ak1";
        String sk = "sk1";
        String region = "region1";
        RequestMessage request = spy(new RequestMessage());
        when(request.getRequestDateTime()).thenReturn(DateUtil.parseIso8601Date("2025-06-03T10:52:35.638Z"));
        request.setMethod(HttpMethod.POST);
        request.setEndpoint(new URI("https://123.mns.cn-hangzhou.aliyuncs.com"));
        request.setResourcePath("/queues/TestQueue?num=10&paramx=100");
        request.addParameter("param1", "abc");
        request.addParameter("PARAM2", "d  ef");
        request.addParameter("PARAM3", "[def]#bbb");
        request.addParameter("PARAM4", "中文参数");

        String authorization = signer.getAuthorization(ak, sk, request, region);
        Assert.assertEquals("MNS4-HMAC-SHA256 Credential=ak1/20250603/region1/mns/aliyun_v4_request,Signature=8d555514e749185d3e6d68df416692e6cc960ba85a089c9d69a2f4d7bb607560", authorization);
    }
}