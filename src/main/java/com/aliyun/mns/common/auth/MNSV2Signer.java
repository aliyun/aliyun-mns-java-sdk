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

package com.aliyun.mns.common.auth;

import com.aliyun.mns.common.MNSConstants;
import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.utils.CodingUtils;
import com.aliyun.mns.common.utils.DateUtil;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 采用 Hmac-SHA1 对SK进行简单的签名，称为V2签名。
 */
public class MNSV2Signer implements RequestSigner {

    private ServiceSignature serviceSignature = new HmacSHA1Signature();

    @Override
    public String getAuthorization(String accessKeyId, String accessKeySecret,
                                   RequestMessage request, String region) {
        CodingUtils.assertStringNotEmpty(accessKeyId, "accessKeyId");
        CodingUtils.assertStringNotEmpty(accessKeySecret, "accessKeySecret");
        CodingUtils.assertParameterNotNull(request.getEndpoint(), "endpoint");
        CodingUtils.assertParameterNotNull(request.getMethod(), "method");
        CodingUtils.assertParameterNotNull(request, "request");
        String contentToSign = getContentToSign(request);
        return buildAuthorization(accessKeyId, getSignature(accessKeySecret, contentToSign));
    }

    private String getContentToSign(RequestMessage request) {
        Map<String, String> headers = request.getHeaders();
        StringBuilder canonicalizedMnsHeaders = new StringBuilder();
        StringBuilder stringToSign = new StringBuilder();
        String contentMd5 = safeGetHeader(MNSConstants.CONTENT_MD5, headers);
        String contentType = safeGetHeader(MNSConstants.CONTENT_TYPE, headers);
        String rfc822FormatDate = DateUtil.formatRfc822Date(request.getRequestDateTime());
        String canonicalizedResource = getRelativeResourcePath(request.getEndpoint(), request.getResourcePath());

        TreeMap<String, String> tmpHeaders = sortHeader(request.getHeaders());
        if (!tmpHeaders.isEmpty()) {
            Set<String> keySet = tmpHeaders.keySet();
            for (String key : keySet) {
                if (key.toLowerCase().startsWith(MNSConstants.X_HEADER_MNS_PREFIX)) {
                    canonicalizedMnsHeaders.append(key).append(":")
                        .append(tmpHeaders.get(key)).append("\n");
                }
            }
        }
        stringToSign.append(request.getMethod()).append("\n")
            .append(contentMd5).append("\n")
            .append(contentType).append("\n")
            .append(rfc822FormatDate).append("\n")
            .append(canonicalizedMnsHeaders)
            .append(canonicalizedResource);
        return stringToSign.toString();
    }

    private String getRelativeResourcePath(URI endpoint, String subPath) {
        String rootPath = endpoint.getPath();
        if (subPath != null && !"".equals(subPath.trim())) {
            if (subPath.startsWith("/")) {
                subPath = subPath.substring(1);
            }
            if (!rootPath.endsWith("/")) {
                return rootPath + "/" + subPath;
            }
            return rootPath + subPath;
        }
        return rootPath;
    }

    private String getSignature(String accessKeySecret, String contentToSign) {
        return serviceSignature.computeHashAndBase64Encode(accessKeySecret, contentToSign);
    }

    /**
     * 构建包含 ak 和 sk密钥签名 的V2签名认证信息
     */
    private static String buildAuthorization(String ak, String signature) {
        return String.format("MNS %s:%s", ak, signature);
    }

    private static TreeMap<String, String> sortHeader(
        Map<String, String> headers) {
        TreeMap<String, String> tmpHeaders = new TreeMap<String, String>();
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            if (key.toLowerCase().startsWith(MNSConstants.X_HEADER_MNS_PREFIX)) {
                tmpHeaders.put(key.toLowerCase(), headers.get(key));
            } else {
                tmpHeaders.put(key, headers.get(key));
            }
        }
        return tmpHeaders;
    }

    private static String safeGetHeader(String key, Map<String, String> headers) {
        if (headers == null) {
            return "";
        }
        String value = headers.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }

}