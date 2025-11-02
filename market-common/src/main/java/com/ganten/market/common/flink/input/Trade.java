package com.ganten.market.common.flink.input;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import com.ganten.market.common.flink.BaseObject;
import com.ganten.market.common.utils.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Trade extends BaseObject {
    private long id;
    private BigDecimal price;
    private BigDecimal volume;
    private boolean isBuyerMaker;
    private long time;

    public String toString() {
        return JsonUtils.toJson(this);
    }

    public byte[] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
