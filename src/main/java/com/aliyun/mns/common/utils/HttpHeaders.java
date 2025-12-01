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

/**
 *
 */
package com.aliyun.mns.common.utils;

/**
 * Contains the common HTTP headers.
 *
 * 
 */
public interface HttpHeaders {

    String AUTHORIZATION = "Authorization";
    String CACHE_CONTROL = "Cache-Control";
    String CONTENT_DISPOSITION = "Content-Disposition";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_MD5 = "Content-MD5";
    String CONTENT_TYPE = "Content-Type";
    String DATE = "Date";
    String ETAG = "ETag";
    String EXPIRES = "Expires";
    String HOST = "Host";
    String LAST_MODIFIED = "Last-Modified";
    String RANGE = "Range";
    String LOCATION = "Location";
    String USER_AGENT = "User-Agent";
    String SECURITY_TOKEN = "security-token";
    String MNS_USER_REQUEST_ID = "x-mns-user-request-id";

}
