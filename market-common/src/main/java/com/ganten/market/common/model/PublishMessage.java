package com.ganten.market.common.model;

import com.ganten.market.common.utils.JsonUtils;
import lombok.Data;

@Data
public class PublishMessage {
    private String mqttTopic;
    private String payload;
    private long timestamp;

    public String toString() {
        return JsonUtils.toJson(this);
    }
}
