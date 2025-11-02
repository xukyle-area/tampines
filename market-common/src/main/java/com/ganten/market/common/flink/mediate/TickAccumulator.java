package com.ganten.market.common.flink.mediate;


import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class TickAccumulator {
    private BigDecimal highest = null;
    private BigDecimal lowest = null;
    private BigDecimal volume = BigDecimal.ZERO;
    private BigDecimal firstPrice = null;
    private BigDecimal lastPrice = null;
}
