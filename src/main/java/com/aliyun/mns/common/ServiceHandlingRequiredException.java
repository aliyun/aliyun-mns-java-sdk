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

package com.aliyun.mns.common;

/**
 * <p>
 * 表示阿里云服务返回的错误消息。此类型要求用户强制进行异常捕获和处理，避免预期外风险
 * </p>
 *
 */
public class ServiceHandlingRequiredException extends Exception {

    protected String errorCode;
    private String requestId;
    private String hostId;

    /**
     * 构造新实例。
     */
    public ServiceHandlingRequiredException() {
        super();
        this.errorCode = "";
    }


    /**
     * 用给定的异常信息构造新实例。
     */
    public ServiceHandlingRequiredException(String message, String errorCode,String requestId,String hostId) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.hostId = hostId;
    }

    /**
     * 用表示异常原因的对象构造新实例。
     *
     * @param cause 异常原因。
     */
    public ServiceHandlingRequiredException(Throwable cause) {
        super(cause);
        this.errorCode = "";
    }

    /**
     * 用给定的异常信息构造新实例。
     *
     */
    public ServiceHandlingRequiredException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "";
    }

    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     *
     * @param message   异常信息。
     * @param cause     异常原因。
     * @param errorCode 错误代码。
     * @param requestId Request ID。
     * @param hostId    Host ID。
     */
    public ServiceHandlingRequiredException(String message, Throwable cause,
                                            String errorCode, String requestId, String hostId) {
        this(message, cause);

        if (errorCode != null) {
            this.errorCode = errorCode;
        }
        this.requestId = requestId;
        this.hostId = hostId;
    }

    /**
     * 返回错误代码的字符串表示。
     *
     * @return 错误代码的字符串表示。
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 返回Request标识。
     *
     * @return Request标识。
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 返回Host标识。
     *
     * @return Host标识。
     */
    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return "[Error Code]:" + errorCode + ", "
            + "[Message]:" + getMessage() + ", "
            + "[host]:" + getHostId() + ", "
            + "[RequestId]: " + getRequestId();
    }
}