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

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.HttpMethod;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.comm.ExecutionContext;
import com.aliyun.mns.common.comm.RequestHandler;
import com.aliyun.mns.common.comm.ResponseHandler;
import com.aliyun.mns.common.comm.RetryStrategy;
import com.aliyun.mns.common.utils.HttpUtil;
import com.aliyun.mns.common.utils.ResourceManager;
import com.aliyun.mns.common.utils.ServiceConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client that accesses Aliyun services.
 *
 * 
 */
public abstract class ServiceClient {

    private static final int DEFAULT_MARK_LIMIT = 1024 * 4;
    private static final Logger log = LoggerFactory.getLogger(ServiceClient.class);
    private static ResourceManager rm = ResourceManager
        .getInstance(ServiceConstants.RESOURCE_NAME_COMMON);
    protected ClientConfiguration config;

    protected ServiceClient(ClientConfiguration config) {
        this.config = config;
    }

    public ClientConfiguration getClientConfiguration() {
        try {
            return (ClientConfiguration) this.config.clone();
        } catch (CloneNotSupportedException ex) {
            // this should not happen
            return null;
        }
    }

    // for internal use, as the config must not be changed
    ClientConfiguration getClientConfigurationNoClone() {
        return this.config;
    }

    public <T> Future<HttpResponse> sendRequest(RequestMessage request,
        ExecutionContext context,

        HttpCallback<T> callback) throws ServiceException {

        //do not check request state at reactor wrapper class
        //give chances to check and restart reactor later
        //if (!isOpen()) {
        //    throw new ClientException("Client is already closed!", callback.getUserRequestId());
        //}
        return sendRequestImpl(request, context, callback);
    }

    private <T> Future<HttpResponse> sendRequestImpl(RequestMessage request,
        ExecutionContext context,
        HttpCallback<T> callback)
        throws ClientException, ServiceException {

        RetryStrategy retryStrategy = context.getRetryStrategy() != null ? context
            .getRetryStrategy() : this.getDefaultRetryStrategy();

        // Sign the request if a signer is provided.
        if (context.getSigner() != null) {
            context.getSigner().sign(request);
        }

        int retries = 0;
        ResponseMessage response = null;

        InputStream content = request.getContent();

        if (content != null && content.markSupported()) {
            content.mark(DEFAULT_MARK_LIMIT);
        }

        while (true) {
            try {
                if (retries > 0) {
                    pause(retries, retryStrategy, callback.getUserRequestId());
                    if (content != null && content.markSupported()) {
                        content.reset();
                    }
                }
                Request httpRequest = buildRequest(request, context);
                // post process request
                handleRequest(httpRequest, context.getResquestHandlers());
                return sendRequestCore(httpRequest, context, callback);

            } catch (ServiceException ex) {
                // Notice that the response should not be closed in the
                // finally block because if the request is successful,
                // the response should be returned to the callers.
                closeResponseSilently(response);
                if (!shouldRetry(ex, request, response, retries, retryStrategy)) {
                    throw ex;
                }
            } catch (ClientException ex) {
                // Notice that the response should not be closed in the
                // finally block because if the request is successful,
                // the response should be returned to the callers.
                closeResponseSilently(response);
                if (!shouldRetry(ex, request, response, retries, retryStrategy)) {
                    throw ex;
                }
            } catch (Exception ex) {
                // Notice that the response should not be closed in the
                // finally block because if the request is successful,
                // the response should be returned to the callers.
                closeResponseSilently(response);
                throw new ClientException(rm.getFormattedString(
                    "ConnectionError", ex.getMessage()), callback.getUserRequestId(), ex);
            } finally {
                retries++;
            }
        }
    }

    /**
     * Implements the core logic to send requests to Aliyun services.
     *
     * @param request request
     * @param context context
     * @param callback callback
     * @param <T> T
     * @return future
     * @throws Exception exception
     */
    protected abstract <T> Future<HttpResponse> sendRequestCore(
        Request request, ExecutionContext context, HttpCallback<T> callback)
        throws Exception;

    private Request buildRequest(RequestMessage requestMessage,
        ExecutionContext context) throws ClientException {
        Request request = new Request();
        request.setMethod(requestMessage.getMethod());
        request.setHeaders(requestMessage.getHeaders());

        // The header must be converted after the request is signed,
        // otherwise the signature will be incorrect.
        if (request.getHeaders() != null) {
            HttpUtil.convertHeaderCharsetToIso88591(request.getHeaders());
        }

        final String delimiter = "/";
        String uri = requestMessage.getEndpoint().toString();
        if (!uri.endsWith(delimiter)
            && (requestMessage.getResourcePath() == null || !requestMessage
            .getResourcePath().startsWith(delimiter))) {
            uri += delimiter;
        }

        if (requestMessage.getResourcePath() != null) {
            uri += requestMessage.getResourcePath();
        }

        String paramString;
        try {
            paramString = HttpUtil.paramToQueryString(
                requestMessage.getParameters(), context.getCharset());
        } catch (UnsupportedEncodingException e) {
            // Assertion error because the caller should guarantee the charset.
            throw new AssertionError(rm.getFormattedString("EncodingFailed",
                e.getMessage()));
        }
        /*
         * For all non-POST requests, and any POST requests that already have a
         * payload, we put the encoded params directly in the URI, otherwise,
         * we'll put them in the POST request's payload.
         */
        boolean requestHasNoPayload = requestMessage.getContent() != null;
        boolean requestIsPost = requestMessage.getMethod() == HttpMethod.POST;
        boolean putParamsInUri = !requestIsPost || requestHasNoPayload;
        if (paramString != null && putParamsInUri) {
            uri += "?" + paramString;
        }

        request.setUrl(uri);

        if (requestIsPost && requestMessage.getContent() == null
            && paramString != null) {
            // Put the param string to the request body if POSTing and
            // no content.
            try {
                byte[] buf = paramString.getBytes(context.getCharset());
                ByteArrayInputStream content = new ByteArrayInputStream(buf);
                request.setContent(content);
                request.setContentLength(buf.length);
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(rm.getFormattedString(
                    "EncodingFailed", e.getMessage()));
            }
        } else {
            request.setContent(requestMessage.getContent());
            request.setContentLength(requestMessage.getContentLength());
        }

        return request;
    }

    private void handleResponse(ResponseMessage response,
        List<ResponseHandler> responseHandlers) throws ServiceException,
        ClientException {
        for (ResponseHandler h : responseHandlers) {
            h.handle(response);
        }
    }

    private void handleRequest(Request message,
        List<RequestHandler> resquestHandlers) throws ServiceException,
        ClientException {
        for (RequestHandler h : resquestHandlers) {
            h.handle(message);
        }

    }

    private void pause(int retries, RetryStrategy retryStrategy, String userRequestId)
        throws ClientException {

        long delay = retryStrategy.getPauseDelay(retries);

        log.debug("Retriable error detected, will retry in " + delay
            + "ms, attempt number: " + retries);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new ClientException(e.getMessage(), userRequestId, e);
        }
    }

    private boolean shouldRetry(Exception exception, RequestMessage request,
        ResponseMessage response, int retries, RetryStrategy retryStrategy) {

        if (retries >= config.getMaxErrorRetry()) {
            return false;
        }

        if (!request.isRepeatable()) {
            return false;
        }

        if (retryStrategy.shouldRetry(exception, request, response, retries)) {
            log.debug("Retrying on " + exception.getClass().getName() + ": "
                + exception.getMessage());
            return true;
        }
        return false;
    }

    private void closeResponseSilently(ResponseMessage response) {

        if (response != null) {
            try {
                response.close();
            } catch (IOException ioe) { /* silently close the response. */
            }
        }
    }

    abstract int ref();

    abstract int unRef();

    protected abstract void close();

    public abstract boolean isOpen();

    protected abstract RetryStrategy getDefaultRetryStrategy();

    /**
     * A wrapper class to HttpMessage. It contains the data to create
     * HttpRequestBase, and it is easy for testing to verify the built data such
     * as URL, content.
     *
     * 
     */
    public static class Request extends HttpMesssage {
        private String uri;
        private HttpMethod method;

        public Request() {

        }

        public String getUri() {
            return this.uri;
        }

        public void setUrl(String uri) {
            this.uri = uri;
        }

        /**
         * @return the method
         */
        public HttpMethod getMethod() {
            return method;
        }

        /**
         * @param method the method to set
         */
        public void setMethod(HttpMethod method) {
            this.method = method;
        }
    }
}
