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

package com.aliyun.mns.common.comm;

import com.aliyun.mns.common.auth.RequestSigner;
import com.aliyun.mns.common.utils.ServiceConstants;
import java.util.LinkedList;
import java.util.List;

/**
 * The context information.
 */
public class ExecutionContext {
    private String charset = ServiceConstants.DEFAULT_ENCODING;
    private RequestSigner signer;
    // The request handlers that handle request content in as a pipeline
    private List<RequestHandler> requestHandlers = new LinkedList<RequestHandler>();
    // The response handlers that handle response message in as a pipeline.
    private List<ResponseHandler> responseHandlers = new LinkedList<ResponseHandler>();
    private RetryStrategy retryStrategy;

    /**
     * Constructor.
     */
    public ExecutionContext() {
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * Returns the default encoding (charset). Default: "UTF-8"
     *
     * @return charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the default encoding (charset). Default: "UTF-8"
     *
     * @param defaultEncoding default encoding
     */
    public void setCharset(String defaultEncoding) {
        this.charset = defaultEncoding;
    }

    /**
     * @return the responseHandlers
     */
    public List<ResponseHandler> getResponseHandlers() {
        return responseHandlers;
    }

    public void addResponseHandler(ResponseHandler handler) {
        responseHandlers.add(handler);
    }

    public void insertResponseHandler(int position, ResponseHandler handler) {
        responseHandlers.add(position, handler);
    }

    public void removeResponseHandler(ResponseHandler handler) {
        responseHandlers.remove(handler);
    }

    /**
     * @return the requestHandlers
     */
    public List<RequestHandler> getResquestHandlers() {
        return requestHandlers;
    }

    public void addRequestHandler(RequestHandler handler) {
        requestHandlers.add(handler);
    }

    public void insertRequestHandler(int position, RequestHandler handler) {
        requestHandlers.add(position, handler);
    }

    public void removeRequestHandler(RequestHandler handler) {
        requestHandlers.remove(handler);
    }
}
