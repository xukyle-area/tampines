package com.ganten.market.common.pojo;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PublishMessage {
    private String mqttTopic;
    private byte[] payload;
    private long timestamp;

    public String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PublishMessage p = new PublishMessage();

        public Builder mqttTopic(String v) {
            p.mqttTopic = v;
            return this;
        }

        public Builder payload(byte[] v) {
            p.payload = v;
            return this;
        }

        public Builder timestamp(long v) {
            p.timestamp = v;
            return this;
        }

        public PublishMessage build() {
            return p;
        }
    }

    /**
     * Serialize into a compact, self-contained binary format. Layout:
     * [int32 topicLen][topic UTF-8 bytes][int32 payloadLen][payload bytes][int64 timestamp]
     * This avoids a hard dependency on generated protobuf classes. Consumers can parse
     * this format with the same layout if they don't have the proto available.
     */
    public byte[] toByteArray() {
        byte[] topicBytes = this.mqttTopic != null ? this.mqttTopic.getBytes(StandardCharsets.UTF_8) : new byte[0];
        byte[] payloadBytes = this.payload != null ? this.payload : new byte[0];

        int capacity = Integer.BYTES + topicBytes.length + Integer.BYTES + payloadBytes.length + Long.BYTES;
        ByteBuffer bb = ByteBuffer.allocate(capacity);
        bb.putInt(topicBytes.length);
        if (topicBytes.length > 0)
            bb.put(topicBytes);
        bb.putInt(payloadBytes.length);
        if (payloadBytes.length > 0)
            bb.put(payloadBytes);
        bb.putLong(this.timestamp);
        return bb.array();
    }
}
