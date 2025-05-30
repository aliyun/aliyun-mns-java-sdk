package com.aliyun.mns.model;

/**
 * @author haolong
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
    BAGGAGE("baggage"),
    /**
     * DLQ message type
     */
    DLQ_MESSAGE_TYPE("DLQMessageType"),
    /**
     * DLQ source ARN
     */
    DLQ_SOURCE_ARN("DLQSourceArn"),
    /**
     * DLQ origin message ID
     */
    DLQ_ORIGIN_MESSAGE_ID("DLQOriginMessageId");

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
