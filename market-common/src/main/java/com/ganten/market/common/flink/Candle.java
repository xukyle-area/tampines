package com.ganten.market.common.flink;

import java.nio.charset.StandardCharsets;
import com.ganten.market.common.utils.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Candle extends BaseObject {
    private String startTime;
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;

    public String toString() {
        return JsonUtils.toJson(this);
    }

    public byte[] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
