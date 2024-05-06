package com.alibaba.mns.client;

import com.aliyun.mns.client.*;
import com.aliyun.mns.client.impl.queue.CreateQueueAction;
import com.aliyun.mns.client.impl.topic.CreateTopicAction;
import com.aliyun.mns.client.impl.topic.SetSubscriptionAttrAction;
import com.aliyun.mns.client.impl.topic.SubscribeAction;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMeta;
import com.aliyun.mns.model.request.queue.CreateQueueRequest;
import com.aliyun.mns.model.request.topic.CreateTopicRequest;
import com.aliyun.mns.model.request.topic.SetSubscriptionAttrRequest;
import com.aliyun.mns.model.request.topic.SubscribeRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CreateTopicAction.class, MNSClient.class, CloudTopic.class, CreateQueueAction.class,
        SubscribeAction.class, CloudQueue.class})
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*", "javax.crypto.*"})
public class CloudTopicTest {

    private MNSClient mnsClient;
    private CloudTopic topic;
    private CloudQueue queue;
    private final String topicName = "hdp-testTopic";
    private final String queueName = "hdp-testQueue";

    @Mock
    private CreateTopicAction createTopicAction;

    @Mock
    private SubscribeAction subscribeAction;

    @Mock
    private CreateQueueAction createQueueAction;

    @Mock
    private SetSubscriptionAttrAction setSubscriptionAttrAction;


    @Before
    public void init() throws Exception {
        mnsClient = getMnsClient();

        // 使用PowerMockito来模拟构造器
        PowerMockito.whenNew(CreateQueueAction.class)
                .withAnyArguments()
                .thenReturn(createQueueAction);
        PowerMockito.whenNew(CreateTopicAction.class)
                .withAnyArguments()
                .thenReturn(createTopicAction);
        PowerMockito.whenNew(SubscribeAction.class)
                .withAnyArguments()
                .thenReturn(subscribeAction);
        PowerMockito.whenNew(SetSubscriptionAttrAction.class)
                .withAnyArguments()
                .thenReturn(setSubscriptionAttrAction);
    }

    @Test
    public void testSubscribeNameIsNull() {
        // create topic and queue
        createTopicAndQueue(mnsClient);
        // create subscription
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        when(subscribeAction.executeWithCustomHeaders(any(SubscribeRequest.class), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn("subscriptionName");

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.subscribe(subMeta);
            }
        });

        verify(subscribeAction, times(0)).executeWithCustomHeaders(any(SubscribeRequest.class), ArgumentMatchers.<Map<String, String>>isNull());

    }

    @Test
    public void testAsyncSubscribeNameIsNull() {
        final AsyncCallback<String> callback = null;
        // create tipic and queue
        createTopicAndQueue(mnsClient);
        // create subscription
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));


        AsyncResult mockedAsyncResult = new AsyncResult() {
            @Override
            public Object getResult() {
                return "subscriptionName";
            }

            @Override
            public Object getResult(long timewait) {
                return null;
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public Exception getException() {
                return null;
            }

            @Override
            public void setTimewait(long timewait) {

            }

            @Override
            public void setFuture(Future future) {
            }
        };

        when(subscribeAction.executeWithCustomHeaders(any(SubscribeRequest.class), (AsyncCallback) isNull(), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn(mockedAsyncResult);

        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.asyncSubscribe(subMeta, callback);
            }
        });

        verify(subscribeAction, times(0)).executeWithCustomHeaders(any(SubscribeRequest.class), (AsyncCallback) isNull(),ArgumentMatchers.<Map<String, String>>isNull());
    }

    @Test
    public void testSubscriptionAttrNameIsNull() {

        // create tipic and queue
        createTopicAndQueue(mnsClient);
        // create subscription
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));
        when(setSubscriptionAttrAction.executeWithCustomHeaders(any(SetSubscriptionAttrRequest.class), ArgumentMatchers.<Map<String, String>>isNull())).thenThrow(new UnfinishedStubbingException("unfinishedStubbingException"));
        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.setSubscriptionAttr(subMeta);
            }
        });
        verify(setSubscriptionAttrAction, times(0)).executeWithCustomHeaders(any(SetSubscriptionAttrRequest.class), ArgumentMatchers.<Map<String, String>>isNull());
    }

    @Test
    public void testAsyncSetSubscriptionAttrNameIsNull() {
        final AsyncCallback<Void> callback = null;
        // create tipic and queue
        createTopicAndQueue(mnsClient);
        // create subscription
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(null);
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));

        AsyncResult mockedAsyncResult = new AsyncResult() {
            @Override
            public Object getResult() {
                return "subscriptionName";
            }

            @Override
            public Object getResult(long timewait) {
                return null;
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public Exception getException() {
                return null;
            }

            @Override
            public void setTimewait(long timewait) {

            }

            @Override
            public void setFuture(Future future) {

            }
        };
        when(setSubscriptionAttrAction.executeWithCustomHeaders(any(SetSubscriptionAttrRequest.class), (AsyncCallback) isNull(), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn(mockedAsyncResult);
        Assert.assertThrows("subscriptionName can not be empty.", NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                topic.asyncSetSubscriptionAttr(subMeta, callback);
            }
        });
        verify(setSubscriptionAttrAction, times(0)).executeWithCustomHeaders(any(SetSubscriptionAttrRequest.class), (AsyncCallback) isNull(), ArgumentMatchers.<Map<String, String>>isNull());
    }
    @Test
    public void testSubscribe() {
        // create topic and queue
        createTopicAndQueue(mnsClient);
        // create subscription
        final SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName("subscibeName");
        subMeta.setEndpoint(topic.generateQueueEndpoint(queueName));
        String expectedSubscriptionUrl = "http://example.com/subscriptions/subscibeName";
        when(subscribeAction.executeWithCustomHeaders(any(SubscribeRequest.class), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn(expectedSubscriptionUrl);
        String actualSubscriptionUrl = topic.subscribe(subMeta);
        Assert.assertEquals("Expected subscription URL does not match the actual one",expectedSubscriptionUrl, actualSubscriptionUrl);

        verify(subscribeAction, times(1)).executeWithCustomHeaders(any(SubscribeRequest.class), ArgumentMatchers.<Map<String, String>>isNull());

    }

    private MNSClient getMnsClient() {
        String endpoint = "http://xxx.mns.test.com";
        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(endpoint);

        MNSClient mnsClient = account.getMNSClient();
        return mnsClient;
    }

    private void createTopicAndQueue(MNSClient mnsClient) {
        QueueMeta queueMeta = new QueueMeta();
        queueMeta.setQueueName(queueName);
        queueMeta.setPollingWaitSeconds(15);
        queueMeta.setMaxMessageSize(2048L);

        when(createQueueAction.executeWithCustomHeaders(any(CreateQueueRequest.class), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn("queueName");

        queue = mnsClient.createQueue(queueMeta);

        verify(createQueueAction, times(1)).executeWithCustomHeaders(any(CreateQueueRequest.class), ArgumentMatchers.<Map<String, String>>isNull());

        queue = mnsClient.createQueue(queueMeta);
        TopicMeta topicMeta = new TopicMeta();
        topicMeta.setTopicName(topicName);

        when(createTopicAction.executeWithCustomHeaders(any(CreateTopicRequest.class), ArgumentMatchers.<Map<String, String>>isNull())).thenReturn("topicName");

        topic = mnsClient.createTopic(topicMeta);

        verify(createTopicAction, times(1)).executeWithCustomHeaders(any(CreateTopicRequest.class), ArgumentMatchers.<Map<String, String>>isNull());
    }


}
