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

import com.aliyun.mns.common.utils.BinaryUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 采用哈希算法进行签名的处理类
 */
public abstract class ServiceSignature {

    private static final Object LOCK = new Object();

    protected volatile Mac macInstance;

    protected ServiceSignature() {
        lazyInitMacInstance();
    }

    private void lazyInitMacInstance() {
        if (macInstance == null) {
            synchronized (LOCK) {
                if (macInstance == null) {
                    try {
                        macInstance = Mac.getInstance(getAlgorithm());
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 获取签名的算法。
     *
     * @return 签名算法。
     */
    public abstract String getAlgorithm();

    /**
     * 计算签名。
     *
     * @param key  签名所需的密钥，对应于访问的Access Key。
     * @param data 用于计算签名的字符串信息。
     * @return 签名字符串。
     */
    public String computeHashAndBase64Encode(String key, String data) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] signData = sign(keyBytes, dataBytes, macInstance, getAlgorithm());
        return BinaryUtil.toBase64String(signData);
    }

    public byte[] computeHash(byte[] key, byte[] data) {
        return this.sign(key, data, macInstance, getAlgorithm());
    }

    private byte[] sign(byte[] key, byte[] data, Mac macInstance, String algorithm) {
        try {
            Mac mac;
            try {
                mac = (Mac)macInstance.clone();
            } catch (CloneNotSupportedException e) {
                // If it is not clonable, create a new one.
                mac = Mac.getInstance(algorithm);
            }
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unsupported algorithm: " + algorithm, ex);
        } catch (InvalidKeyException ex) {
            throw new RuntimeException("Invalid key", ex);
        }
    }

}
