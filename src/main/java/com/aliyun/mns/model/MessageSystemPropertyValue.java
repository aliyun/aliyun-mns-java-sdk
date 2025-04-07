package com.aliyun.mns.model;

/**
 * @author haolong
 */
public class MessageSystemPropertyValue implements AbstractMessagePropertyValue {
    private SystemPropertyType dataType;
    private String stringValue;

    public MessageSystemPropertyValue(SystemPropertyType type, String value) {
        if (type == null || value == null) {
            throw new IllegalArgumentException("type and value can not be null");
        }
        this.dataType = type;
        this.stringValue = value;
    }

    @Override
    public String toString() {
        return "MessageSystemPropertyValue{" +
            "dataType=" + dataType +
            ", stringValue='" + stringValue + '\'' +
            '}';
    }

    @Override
    public String getStringValueByType() {
        return stringValue;
    }

    @Override
    public String getDataTypeString() {
        return dataType.name();
    }
}
