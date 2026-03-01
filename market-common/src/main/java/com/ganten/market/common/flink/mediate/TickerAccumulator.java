package com.ganten.market.common.flink.mediate;


import java.math.BigDecimal;
import com.ganten.market.common.flink.BaseObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class TickerAccumulator extends BaseObject {
    private BigDecimal highest = null;
    private BigDecimal lowest = null;
    private BigDecimal volume = BigDecimal.ZERO;
    private BigDecimal firstPrice = null;
    private BigDecimal lastPrice = null;
    // 用于 merge 时判断顺序
    private long firstTimestamp = Long.MAX_VALUE;
    private long lastTimestamp = Long.MIN_VALUE;
}
