package com.ganten.market.common.flink;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Tick extends BaseObject {
    // {@link OrderbookJob}
    private BigDecimal ask;
    private BigDecimal bid;
    // from {@link TickJob}
    private BigDecimal last;
    private BigDecimal volume;
    private BigDecimal highest;
    private BigDecimal lowest;
    private BigDecimal change;
    private String changePercent;
}
