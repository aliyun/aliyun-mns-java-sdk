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

package com.aliyun.mns.model;

import java.io.Serializable;

/**
 * Account attributes model
 */
public class AccountAttributes implements Serializable {
    private static final long serialVersionUID = 1159160616345899035L;

    private String loggingBucket;

    private Integer traceEnabled;

    /**
     * Get logging bucket
     *
     * @return logging bucket
     */
    public String getLoggingBucket() {
        return loggingBucket;
    }

    /**
     * Set logging bucket
     *
     * @param loggingBucket logging bucket
     */
    public void setLoggingBucket(String loggingBucket) {
        this.loggingBucket = loggingBucket;
    }

    /**
     * Get trace enabled flag
     *
     * @return trace enabled flag
     */
    public Integer getTraceEnabled() {
        return traceEnabled;
    }

    /**
     * Set trace enabled flag
     *
     * @param traceEnabled trace enabled flag
     */
    public void setTraceEnabled(Integer traceEnabled) {
        this.traceEnabled = traceEnabled;
    }
}