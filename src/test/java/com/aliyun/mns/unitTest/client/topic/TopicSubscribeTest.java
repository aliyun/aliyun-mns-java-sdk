package com.aliyun.mns.unitTest.client.topic;

import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.client.AsyncResult;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MockUtil;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.ServiceCredentials;
import com.aliyun.mns.common.comm.ExecutionContext;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.HttpCallback;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.model.SubscriptionMeta;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TopicSubscribeTest {

    @Mock
    private ServiceClient serviceClient;
    @Mock
    private ServiceCredentials credentials;

    private CloudTopic topic;
    private CloudQueue queue;
    private final String topicName = "hdp-testTopic";
    private final String queueName = "hdp-testQueue";
    private final String endpoint = "http://xxx.mns.test.com";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        queue = MockUtil.spyNewCloudQueue(queueName, serviceClient, credentials, endpoint);
        topic = MockUtil.spyNewCloudTopic(topicName, serviceClient, credentials, endpoint);
    }

    @Test
    public void testSubscribeNameIsNull() throws ServiceException {
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.subscribe(subMeta);
            }
        });
    }

    @Test
    public void testAsyncSubscribeNameIsNull() throws ServiceException {
        final AsyncCallback<String> callback = null;
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.asyncSubscribe(subMeta, callback);
            }
        });

    }

    @Test
    public void testSubscriptionAttrNameIsNull() throws ServiceException {
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.setSubscriptionAttr(subMeta);
            }
        });
    }

    @Test
    public void testAsyncSetSubscriptionAttrNameIsNull() throws ServiceException {
        final AsyncCallback<Void> callback = null;
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.asyncSetSubscriptionAttr(subMeta, callback);
            }
        });
    }

    @Test
    public void testSubscribe() throws ServiceException {
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName("subscibeName");
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        String expectedSubscriptionUrl = "http://example.com/subscriptions/subscibeName";
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        when(serviceClient.getClientConfiguration()).thenReturn(clientConfiguration);
        when(serviceClient.asyncSendRequest(any(RequestMessage.class), any(ExecutionContext.class), any(HttpCallback.class), any(long.class)))
            .thenReturn(new AsyncResult<String>() {
                @Override
                public String getResult() {
                    return expectedSubscriptionUrl;
                }

                @Override
                public String getResult(long timewait) {
                    return expectedSubscriptionUrl;
                }

                @Override
                public boolean isSuccess() {
                    return true;
                }

                @Override
                public Exception getException() {
                    return null;
                }

                @Override
                public void setTimewait(long timewait) {
                }

                @Override
                public void setFuture(Future<HttpResponse> future) {
                }
            });

        String actualSubscriptionUrl = topic.subscribe(subMeta);

        Assert.assertEquals("Expected subscription URL does not match the actual one", expectedSubscriptionUrl, actualSubscriptionUrl);
        verify(serviceClient, times(1)).asyncSendRequest(any(RequestMessage.class), any(ExecutionContext.class), any(HttpCallback.class), any(long.class));
    }

}
