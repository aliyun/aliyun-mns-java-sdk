package com.aliyun.mns.model;

/**
 * @author haolong
 * @date 2025/2/17 16:34
 * @desc 属性值类型
 */
public enum PropertyType {
    /**
     * 数字，包括整数、浮点数、科学计数法等。
     */
    NUMBER,
    /**
     * 字符串，包括普通字符串、unicode字符串等。
     */
    STRING,
    /**
     * 布尔值，包括true、false。
     */
    BOOLEAN,
    /**
     * 二进制数据。
     */
    BINARY
}
