package com.aliyun.mns.model;

/**
 * @author haolong
 * @date 2025/2/24 17:04
 */
public enum MessageSystemPropertyName {
    /**
     * openTelemetry trace parent
     */
    TRACE_PARENT("traceparent"),
    /**
     * openTelemetry trace state
     */
    TRACE_STATE("tracestate"),
    /**
     * baggage
     */
    BAGGAGE("baggage");

    private String value;

    MessageSystemPropertyName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageSystemPropertyName getByValue(String value) {
        for (MessageSystemPropertyName propertyName : MessageSystemPropertyName.values()) {
            if (propertyName.getValue().equals(value)) {
                return propertyName;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }
}
