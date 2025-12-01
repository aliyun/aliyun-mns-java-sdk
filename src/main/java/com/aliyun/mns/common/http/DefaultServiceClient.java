/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.mns.common.http;

import com.aliyun.mns.common.ClientErrorCode;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.comm.ExecutionContext;
import com.aliyun.mns.common.comm.RetryStrategy;
import com.aliyun.mns.common.http.HttpFactory.IdleConnectionMonitor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * The default implementation of <code>ServiceClient</code>.
 */
public class DefaultServiceClient extends ServiceClient {

    boolean clientIsOpen = false;
    private HttpAsyncClient httpClient;
    private PoolingNHttpClientConnectionManager connManager;

    private Integer refCount = 0;

    // this constructor in package visible
    DefaultServiceClient(ClientConfiguration config) {
        super(config);
        connManager = HttpFactory.createConnectionManager(config);
        httpClient = HttpFactory.createHttpAsyncClient(connManager, config);
        this.ref();
    }

    @Override
    synchronized int ref() {
        refCount += 1;
        this.open();
        return refCount;
    }

    @Override
    synchronized int unRef() {
        refCount -= 1;
        if (refCount == 0) {
            this.close();
        }
        return refCount;
    }

    @Override
    public <T> Future<HttpResponse> sendRequestCore(
        ServiceClient.Request request, ExecutionContext context,
        HttpCallback<T> callback) throws IOException {
        assert request != null && context != null;

        HttpRequestBase httpRequest = HttpFactory.createHttpRequest(
            request, context);

        //Execute request, make the exception to the standard WebException
        Future<HttpResponse> future = null;
        try {
            future = httpClient.execute(httpRequest, callback);
        } catch (IllegalStateException e) {
            if (!((CloseableHttpAsyncClient) httpClient).isRunning()) {
                synchronized (this) {
                    //double checked
                    if (!((CloseableHttpAsyncClient) httpClient).isRunning()) {
                        //cannot restart previous client by just doing this.open() here,
                        //so, close old client and create a new one,
                        //notice: old client is abandoned to GC.
                        this.close();
                        connManager = HttpFactory.createConnectionManager(config);
                        httpClient = HttpFactory.createHttpAsyncClient(connManager, config);
                        this.open();
                    }
                }
            }

            //redo the request
            future = httpClient.execute(httpRequest, callback);
        }
        return future;
    }

    private void open() {
        if (this.httpClient != null
            && this.httpClient instanceof CloseableHttpAsyncClient
            && !clientIsOpen) {
            ((CloseableHttpAsyncClient) httpClient).start();
            clientIsOpen = true;
            // start a thread to clean idle and expired connection
            IdleConnectionMonitor.getInstance().addConnMgr(connManager);
        }
    }

    @Override
    public boolean isOpen() {
        return clientIsOpen;
    }

    @Override
    protected void close() {
        HttpFactory.IdleConnectionMonitor.getInstance().removeConnMgr(connManager);
        if (this.httpClient != null
            && this.httpClient instanceof CloseableHttpAsyncClient) {
            try {
                ((CloseableHttpAsyncClient) httpClient).close();
                clientIsOpen = false;
            } catch (IOException e) { // quietly
            }
        }
    }

    protected RetryStrategy getDefaultRetryStrategy() {
        return new DefaultRetryStrategy();
    }

    private static class DefaultRetryStrategy extends RetryStrategy {

        @Override
        public boolean shouldRetry(Exception ex, RequestMessage request,
            ResponseMessage response, int retries) {
            if (ex instanceof ClientException) {
                String errorCode = ((ClientException) ex).getErrorCode();
                if (errorCode.equals(ClientErrorCode.CONNECTION_TIMEOUT)
                    || errorCode.equals(ClientErrorCode.SOCKET_TIMEOUT)) {
                    return true;
                }
            }

            if (response != null) {
                int statusCode = response.getStatusCode();
                if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR
                    || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    return true;
                }
            }

            return false;
        }
    }
}
