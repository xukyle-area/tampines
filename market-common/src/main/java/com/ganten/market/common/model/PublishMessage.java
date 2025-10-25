package com.ganten.market.common.model;

import java.nio.charset.StandardCharsets;
import com.ganten.market.common.utils.JsonUtils;
import lombok.Data;

@Data
public class PublishMessage {
    private String mqttTopic;
    private byte[] payload;
    private long timestamp;

    public String toString() {
        return JsonUtils.toJson(this);
    }

    public byte[] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
