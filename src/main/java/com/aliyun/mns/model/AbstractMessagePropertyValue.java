package com.aliyun.mns.model;

/**
 * Abstract interface for message property values
 *
 * @author haolong
 */
public interface AbstractMessagePropertyValue {
    /**
     * Get string value by type
     * @return string value
     */
    String getStringValueByType();

    /**
     * Get data type as string
     * @return data type string
     */
    String getDataTypeString();
}