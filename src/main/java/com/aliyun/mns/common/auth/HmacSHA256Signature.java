package com.aliyun.mns.common.auth;

/**
 * 采用 HmacSHA256 哈希算法进行签名的处理类。
 */
public class HmacSHA256Signature extends ServiceSignature {
    public static final String ALGORITHM = "HmacSHA256";

    public HmacSHA256Signature() {
        super();
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

}

