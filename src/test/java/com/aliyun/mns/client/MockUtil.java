package com.aliyun.mns.client;

import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.http.ServiceClient;
import org.mockito.Mockito;

import java.net.URI;

/**
 * @author yuanzhi
 * @date 2025/5/6.
 */
public class MockUtil {

    public static MNSClient spyNewMnsClient(ServiceCredentials credentials, ServiceClient serviceClient, String endpoint, String region) {
        MNSClient mnsClient = new DefaultMNSClient(credentials, serviceClient, endpoint, region);
        return Mockito.spy(mnsClient);
    }

    public static CloudQueue spyNewCloudQueue(String queueName, ServiceClient serviceClient, ServiceCredentials credentials, String endpoint) {
        CloudQueue queue = new CloudQueue(queueName, serviceClient, credentials, URI.create(endpoint));
        return Mockito.spy(queue);
    }

    public static CloudTopic spyNewCloudTopic(String topicName, ServiceClient serviceClient, ServiceCredentials credentials, String endpoint) {
        CloudTopic topic = new CloudTopic(topicName, serviceClient, credentials, URI.create(endpoint));
        return Mockito.spy(topic);
    }

}
