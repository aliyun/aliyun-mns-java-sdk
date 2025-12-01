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

package com.aliyun.mns.client;

import java.util.concurrent.Future;
import org.apache.http.HttpResponse;

/**
 * asynchronous call result
 *
 * @param <T> type of Result model
 */
public interface AsyncResult<T> {
    /**
     * @return the result aysnc call return,
     * not null meaning async call successful,
     * wait result until call end.
     */
    T getResult();

    /**
     * @param timewait wait for result in 'timewait' milliseconds.
     * @return as async call result.
     */
    T getResult(long timewait);

    /**
     * @return async call is successful(true) or not (false)
     */
    boolean isSuccess();

    /**
     * @return async call exception
     */
    Exception getException();

    /**
     * @param timewait wait for result in 'timewait' milliseconds.
     */
    void setTimewait(long timewait);

    /**
     * @param future thre http client Future response
     */
    void setFuture(Future<HttpResponse> future);
}
