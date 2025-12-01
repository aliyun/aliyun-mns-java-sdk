package com.aliyun.mns.unitTest.client.common;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.DefaultMNSClient;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.impl.AbstractAction;
import com.aliyun.mns.client.impl.queue.SendMessageAction;
import com.aliyun.mns.common.auth.MNSV2Signer;
import com.aliyun.mns.common.auth.MNSV4Signer;
import com.aliyun.mns.common.auth.RequestSigner;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.SystemPropertiesCredentialsProvider;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author yuanzhi
 * @date 2025/6/9.
 */
public class ServiceClientFactoryTest {

    @Test
    public void testCreateServiceClient() throws Exception {
        String region = "cn-shanghai";
        String endpoint = "https://123.mns.cn-shanghai.aliyuncs.com";
        SystemPropertiesCredentialsProvider provider = new SystemPropertiesCredentialsProvider();

        CloudAccount accountWithV2Sign = createCloudAccount(region, endpoint, provider, SignVersion.V2);
        MNSClient mnsClientV2Sign = accountWithV2Sign.getMNSClient();
        Assert.assertNotNull(mnsClientV2Sign);
        ServiceClient serviceClientV2Sign = getPrivateServiceClient(mnsClientV2Sign);
        Assert.assertNotNull(serviceClientV2Sign);
        SendMessageAction actionV2Sign = new SendMessageAction(serviceClientV2Sign, null, null);
        RequestSigner requestSigner = getPrivateRequestSigner(actionV2Sign);
        Assert.assertNotNull(requestSigner);
        Assert.assertTrue(requestSigner instanceof MNSV2Signer);

        CloudAccount accountWithV4Sign = createCloudAccount(region, endpoint, provider, SignVersion.V4);
        MNSClient mnsClientV4Sign = accountWithV4Sign.getMNSClient();
        Assert.assertNotNull(mnsClientV4Sign);
        ServiceClient serviceClientV4Sign = getPrivateServiceClient(mnsClientV4Sign);
        Assert.assertNotNull(serviceClientV4Sign);
        SendMessageAction actionV4Sign = new SendMessageAction(serviceClientV4Sign, null, null);
        requestSigner = getPrivateRequestSigner(actionV4Sign);
        Assert.assertNotNull(requestSigner);
        Assert.assertTrue(requestSigner instanceof MNSV4Signer);
    }

    private static CloudAccount createCloudAccount(String region, String accountEndPoint,
                                                   AlibabaCloudCredentialsProvider provider, SignVersion signVersion) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(signVersion);

        return new CloudAccount(
            null,
            null,
            accountEndPoint,
            null,
            provider,
            clientConfig,
            region);
    }

    private static ServiceClient getPrivateServiceClient(MNSClient mnsClient) throws NoSuchFieldException, IllegalAccessException {
        Field field = DefaultMNSClient.class.getDeclaredField("serviceClient");
        field.setAccessible(true); // 设置为可访问私有字段
        return (ServiceClient)field.get(mnsClient);
    }

    public static RequestSigner getPrivateRequestSigner(AbstractAction<?, ?> action) throws NoSuchFieldException, IllegalAccessException {
        // 获取实际类的父类
        Class<?> parentClass = action.getClass().getSuperclass();

        // 获取父类中的 requestSigner 字段
        Field field = parentClass.getDeclaredField("requestSigner");
        field.setAccessible(true); // 设置为可访问私有字段

        return (RequestSigner)field.get(action);
    }

}
