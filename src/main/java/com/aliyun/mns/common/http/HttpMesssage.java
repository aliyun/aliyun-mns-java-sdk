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

import com.aliyun.mns.common.utils.CaseInsensitiveMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The base class for message of HTTP request and response.
 *
 * 
 */
public abstract class HttpMesssage {

    private Map<String, String> headers = new CaseInsensitiveMap<String>();
    private InputStream content;
    private long contentLength;

    protected HttpMesssage() {
        super();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        assert (headers != null);
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        if (this.headers.containsKey(key)) {
            return this.headers.get(key);
        } else {
            return "";
        }
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void close() throws IOException {
        if (content != null) {
            content.close();
            content = null;
        }
    }
}
