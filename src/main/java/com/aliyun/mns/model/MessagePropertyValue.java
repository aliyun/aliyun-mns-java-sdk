package com.aliyun.mns.model;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_CHARSET;

/**
 * @author haolong
 * @date 2025/2/17 16:34
 * @desc 消息属性值
 */
public class MessagePropertyValue implements AbstractMessagePropertyValue {
    private PropertyType dataType;
    private String stringValue;
    private byte[] binaryValue;

    public MessagePropertyValue(PropertyType type, String value) {
        if (type == null || value == null) {
            throw new IllegalArgumentException("type and value can not be null");
        }
        this.dataType = type;
        switch (type) {
            case NUMBER:
                try {
                    // 校验是否是数字
                    Double.parseDouble(value);
                    this.stringValue = value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format: " + value);
                }
                break;
            case STRING:
                this.stringValue = value;
                break;
            case BOOLEAN:
                // 校验是否为合法的布尔值
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                    throw new IllegalArgumentException("Invalid boolean value: " + value);
                }
                this.stringValue = value;
                break;
            case BINARY:
                try {
                    this.binaryValue = value.getBytes(DEFAULT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Not support encoding: " + DEFAULT_CHARSET);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid property type: " + type);
        }
    }

    public MessagePropertyValue(byte[] value)  {
        this.dataType = PropertyType.BINARY;
        this.binaryValue = value;
    }

    public MessagePropertyValue(int value) {
        this.dataType = PropertyType.NUMBER;
        this.stringValue = String.valueOf(value);
    }

    public MessagePropertyValue(long value) {
        this.dataType = PropertyType.NUMBER;
        this.stringValue = String.valueOf(value);
    }

    public MessagePropertyValue(double value) {
        this.dataType = PropertyType.NUMBER;
        this.stringValue = String.valueOf(value);
    }

    public MessagePropertyValue(String value) {
        this.dataType = PropertyType.STRING;
        this.stringValue = value;
    }

    public MessagePropertyValue(boolean value) {
        this.dataType = PropertyType.BOOLEAN;
        this.stringValue = String.valueOf(value);
    }

    public PropertyType getDataType() {
        return dataType;
    }

    public String getStringValue() {
        return stringValue;
    }

    public byte[] getBinaryValue() {
        return binaryValue;
    }

    @Override
    public String getStringValueByType() {
        try {
            switch (dataType) {
                case NUMBER:
                case STRING:
                case BOOLEAN:
                    return stringValue;
                case BINARY:
                    return new String(binaryValue, DEFAULT_CHARSET);
                default:
                    return "";
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Not support encoding: " + DEFAULT_CHARSET);
        }
    }

    @Override
    public String getDataTypeString() {
        return dataType.name();
    }

    @Override
    public String toString() {
        return "MessagePropertyValue{" +
            "dataType=" + dataType +
            ", stringValue='" + stringValue + '\'' +
            ", binaryValue=" + Arrays.toString(binaryValue) +
            '}';
    }
}
