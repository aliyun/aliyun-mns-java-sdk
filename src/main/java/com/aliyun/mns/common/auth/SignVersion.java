package com.aliyun.mns.common.auth;

/**
 * 对访问密钥进行签名的算法版本
 *
 * @author yuanzhi
 * @date 2025/6/2.
 */
public enum SignVersion {
    /**
     * V2签名，会对访问密钥进行简单的签名。
     * @deprecated 推荐改为使用安全性更高的 V4签名。
     */
    @Deprecated
    V2("MNS-SIGN-V2"),
    /**
     * V4签名，引入了时间、Region、服务(产品)三种因素后对访问密钥进行的多轮计算得到的派生签名。
     * <p>V4签名，主要解决 V2签名 无地域限制、无时间限制、无产品限制等全局可用特性从而导致的安全风险问题。
     */
    V4("MNS-SIGN-V4");

    private final String code;

    private SignVersion(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
