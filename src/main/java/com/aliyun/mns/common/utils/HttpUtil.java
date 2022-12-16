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

package com.aliyun.mns.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    private static final String ISO_8859_1_CHARSET = "iso-8859-1";
    private static final String JAVA_CHARSET = "utf-8";

    /**
     * Encode a URL segment with special chars replaced.
     *
     * @param value url
     * @param charset charset
     * @return encoded string
     * @throws UnsupportedEncodingException exception
     */
    // TODO change the method name to percentageEncode
    public static String urlEncode(String value, String charset)
        throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, charset)
            .replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
            : null;
    }

    /**
     * Encodes request parameters to a URL query.
     *
     * @param params params
     * @param charset charset
     * @return string
     * @throws UnsupportedEncodingException exception
     */
    public static String paramToQueryString(Map<String, String> params,
        String charset) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String val = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            paramString.append(key);
            if (val != null) {
                // The query string in URL should be encoded with URLEncoder
                // standard.
                paramString.append("=")
                    .append(HttpUtil.urlEncode(val, charset));
                // TODO: Should use URLEncoder.encode(val, charset)) instead of
                // HttpUril#urlEncode;
            }

            first = false;
        }

        return paramString.toString();
    }

    // To fix the bug that the header value could not be unicode chars.
    // Because HTTP headers are encoded in iso-8859-1,
    // we need to convert the utf-8(java encoding) strings to iso-8859-1 ones.
    public static void convertHeaderCharsetFromIso88591(
        Map<String, String> headers) {
        convertHeaderCharset(headers, ISO_8859_1_CHARSET, JAVA_CHARSET);
    }

    // For response, convert from iso-8859-1 to utf-8.
    public static void convertHeaderCharsetToIso88591(
        Map<String, String> headers) {
        convertHeaderCharset(headers, JAVA_CHARSET, ISO_8859_1_CHARSET);
    }

    private static void convertHeaderCharset(Map<String, String> headers,
        String fromCharset, String toCharset) {
        assert (headers != null);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getValue() == null) {
                continue;
            }

            try {
                header.setValue(new String(header.getValue().getBytes(
                    fromCharset), toCharset));
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("Invalid charset name.");
            }
        }
    }

}
