package com.aliyun.mns.unitTest.client.common;

import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.impl.AbstractAction;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.model.AbstractRequest;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.BasicSessionCredentials;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ MNSClient.class, AbstractRequest.class, ServiceClient.class,
    ServiceCredentials.class, URI.class, AbstractAction.class})
public class AbstractActionTest {


    /**
     * test Case ： SignatureHeader 相关
     * case 1、 同次请求，不管凭证如何刷新， ak、sk 是同套凭证
     */
    @Test
    public void testSignatureHeaderForAKSK() throws Exception {
        ServiceCredentials credentials = PowerMockito.mock(ServiceCredentials.class);

        // 凭证: 初始化凭证
        final AlibabaCloudCredentialsProvider alibabaCloudCredentialsProvider = PowerMockito.mock(AlibabaCloudCredentialsProvider.class);
        PowerMockito.when(credentials.getCredentialsProvider()).thenReturn(alibabaCloudCredentialsProvider);
        final BasicSessionCredentials alibabaCloudCredentialsV1 = PowerMockito.mock(BasicSessionCredentials.class);
        PowerMockito.when(alibabaCloudCredentialsV1.getAccessKeyId()).thenReturn("ak1");
        PowerMockito.when(alibabaCloudCredentialsV1.getAccessKeySecret()).thenReturn("sk1");
        PowerMockito.when(alibabaCloudCredentialsV1.getSessionToken()).thenReturn("token1");
        final BasicSessionCredentials alibabaCloudCredentialsV2 = PowerMockito.mock(BasicSessionCredentials.class);
        PowerMockito.when(alibabaCloudCredentialsV2.getAccessKeyId()).thenReturn("ak2");
        PowerMockito.when(alibabaCloudCredentialsV2.getAccessKeySecret()).thenReturn("sk2");
        PowerMockito.when(alibabaCloudCredentialsV2.getSessionToken()).thenReturn("token2");

        PowerMockito.when(alibabaCloudCredentialsProvider.getCredentials()).thenReturn(alibabaCloudCredentialsV1);
        // 凭证：当调用 getAk 的时候，刷新凭证
        PowerMockito.when(alibabaCloudCredentialsV1.getAccessKeyId()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock mock) throws Throwable {
                // v1 执行后，直接切到 v2 版本
                PowerMockito.when(alibabaCloudCredentialsProvider.getCredentials()).thenReturn(alibabaCloudCredentialsV2);
                return "ak1";
            }
        });

        // init action
        ClientConfiguration configuration = PowerMockito.mock(ClientConfiguration.class);

        ServiceClient client = PowerMockito.mock(ServiceClient.class);
        PowerMockito.when(client.getClientConfiguration()).thenReturn(configuration);
        PowerMockito.when(configuration.isGenerateRequestId()).thenReturn(true);
        URI endpoint = PowerMockito.mock(URI.class);

        AbstractAction<AbstractRequest,Void> action = new AbstractAction<AbstractRequest, Void>(HttpMethod.GET, "testAction", client, credentials, endpoint) {
            @Override
            protected RequestMessage buildRequest(AbstractRequest reqObject) throws ClientException {
                return null;
            }
        };
        AbstractAction<AbstractRequest,Void> spyAction = PowerMockito.spy(action);

        // mock signature
        PowerMockito
            .when(spyAction,"getSignature",any(RequestMessage.class),anyString())
            .thenReturn("sdssss");

        // 执行 签名 method
        RequestMessage request = new RequestMessage();
        Whitebox.invokeMethod(spyAction, "addSignatureHeader", request);

        String authHeader = request.getHeader(MNSConstants.AUTHORIZATION);
        String tokenHeader = request.getHeader(MNSConstants.SECURITY_TOKEN);

        Assert.assertTrue(authHeader.contains("ak1"));
        Assert.assertTrue(tokenHeader.contains("token1"));
    }

    /**
     * test Case ： SignatureHeader 相关
     * case 2、 同次请求，凭证刷新，下次请求使用新的凭证内容
     */
    @Test
    public void testSignatureHeaderForRefresh() throws Exception {
        ServiceCredentials credentials = PowerMockito.mock(ServiceCredentials.class);

        // 凭证: 初始化凭证
        final AlibabaCloudCredentialsProvider alibabaCloudCredentialsProvider = PowerMockito.mock(AlibabaCloudCredentialsProvider.class);
        PowerMockito.when(credentials.getCredentialsProvider()).thenReturn(alibabaCloudCredentialsProvider);

        final BasicSessionCredentials alibabaCloudCredentialsV1 = PowerMockito.mock(BasicSessionCredentials.class);
        PowerMockito.when(alibabaCloudCredentialsV1.getAccessKeyId()).thenReturn("ak1");
        PowerMockito.when(alibabaCloudCredentialsV1.getAccessKeySecret()).thenReturn("sk1");
        PowerMockito.when(alibabaCloudCredentialsV1.getSessionToken()).thenReturn("token1");

        final BasicSessionCredentials alibabaCloudCredentialsV2 = PowerMockito.mock(BasicSessionCredentials.class);
        PowerMockito.when(alibabaCloudCredentialsV2.getAccessKeyId()).thenReturn("ak2");
        PowerMockito.when(alibabaCloudCredentialsV2.getAccessKeySecret()).thenReturn("sk2");
        PowerMockito.when(alibabaCloudCredentialsV2.getSessionToken()).thenReturn("token2");

        // 凭证：两次调用，刷新不通的凭证
        PowerMockito.when(alibabaCloudCredentialsProvider.getCredentials()).thenReturn(alibabaCloudCredentialsV1).thenReturn(alibabaCloudCredentialsV2);


        // init action
        ClientConfiguration configuration = PowerMockito.mock(ClientConfiguration.class);

        ServiceClient client = PowerMockito.mock(ServiceClient.class);
        PowerMockito.when(client.getClientConfiguration()).thenReturn(configuration);
        PowerMockito.when(configuration.isGenerateRequestId()).thenReturn(true);
        URI endpoint = PowerMockito.mock(URI.class);

        AbstractAction<AbstractRequest,Void> action = new AbstractAction<AbstractRequest, Void>(HttpMethod.GET, "testAction", client, credentials, endpoint) {
            @Override
            protected RequestMessage buildRequest(AbstractRequest reqObject) throws ClientException {
                return null;
            }
        };
        AbstractAction<AbstractRequest,Void> spyAction = PowerMockito.spy(action);

        // mock signature
        PowerMockito
            .when(spyAction,"getSignature",any(RequestMessage.class),anyString())
            .thenReturn("sdssss");

        // 执行 签名 method
        RequestMessage request = new RequestMessage();
        Whitebox.invokeMethod(spyAction, "addSignatureHeader", request);

        String authHeader = request.getHeader(MNSConstants.AUTHORIZATION);
        String tokenHeader = request.getHeader(MNSConstants.SECURITY_TOKEN);

        Assert.assertTrue(authHeader.contains("ak1"));
        Assert.assertTrue(tokenHeader.contains("token1"));


        //  第2次执行，用新的 凭证
        request = new RequestMessage();
        Whitebox.invokeMethod(spyAction, "addSignatureHeader", request);

        Assert.assertTrue(request.getHeader(MNSConstants.AUTHORIZATION).contains("ak2"));
        Assert.assertTrue(request.getHeader(MNSConstants.SECURITY_TOKEN).contains("token2"));
    }
}
