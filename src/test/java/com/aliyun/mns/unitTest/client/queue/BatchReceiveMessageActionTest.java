package com.aliyun.mns.unitTest.client.queue;

import com.aliyun.mns.client.impl.queue.BatchReceiveMessageAction;
import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.http.ServiceClient;
import com.aliyun.mns.common.http.ServiceClientFactory;
import com.aliyun.mns.model.request.queue.BatchReceiveMessageRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 *
 */
public class BatchReceiveMessageActionTest {

    private BatchReceiveMessageAction batchReceiveMessageAction;
    private BatchReceiveMessageRequest batchReceiveMessageRequest;

    @Before
    public void setUp() {
        ServiceClient serviceClient = ServiceClientFactory.createServiceClient(new ClientConfiguration());

        batchReceiveMessageAction = new BatchReceiveMessageAction(serviceClient, null, null);
        batchReceiveMessageRequest = buildBatchReceiveMessageRequest();
    }

    @Test
    public void testBuildRequest_WithZeroWaitSeconds() throws Exception {
        Method buildRequestMethod = getBuildRequestMethod();

        batchReceiveMessageRequest.setWaitSeconds(0);

        RequestMessage result = (RequestMessage) buildRequestMethod.invoke(batchReceiveMessageAction, batchReceiveMessageRequest);

        // expect the resource path contains the waitSeconds parameter
        Assert.assertTrue(result.getResourcePath().contains(MNSConstants.PARAM_WAITSECONDS));
    }

    @Test
    public void testBuildRequest_WithoutWaitSeconds() throws Exception {
        Method buildRequestMethod = getBuildRequestMethod();

        RequestMessage result = (RequestMessage) buildRequestMethod.invoke(batchReceiveMessageAction, batchReceiveMessageRequest);

        // expect the resource path does not contain the waitSeconds parameter
        Assert.assertFalse(result.getResourcePath().contains(MNSConstants.PARAM_WAITSECONDS));
    }

    @Test
    public void testBuildRequest_WithInvalidWaitSeconds() throws Exception {
        Method buildRequestMethod = getBuildRequestMethod();

        batchReceiveMessageRequest.setWaitSeconds(-1);

        RequestMessage result = (RequestMessage) buildRequestMethod.invoke(batchReceiveMessageAction, batchReceiveMessageRequest);

        // expect the resource path does not contain the waitSeconds parameter
        Assert.assertFalse(result.getResourcePath().contains(MNSConstants.PARAM_WAITSECONDS));
    }

    private BatchReceiveMessageRequest buildBatchReceiveMessageRequest() {
        BatchReceiveMessageRequest batchReceiveMessageRequest = new BatchReceiveMessageRequest();
        batchReceiveMessageRequest.setBatchSize(10);
        return batchReceiveMessageRequest;
    }

    private Method getBuildRequestMethod() throws NoSuchMethodException {
        Method buildRequestMethod = BatchReceiveMessageAction.class.getDeclaredMethod("buildRequest", BatchReceiveMessageRequest.class);
        buildRequestMethod.setAccessible(true);
        return buildRequestMethod;
    }
}

