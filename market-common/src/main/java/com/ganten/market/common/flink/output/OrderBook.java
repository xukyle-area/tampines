package com.ganten.market.common.flink.output;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.BaseObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderBook extends BaseObject {
    private Market market;
    private double grouping;
    private long startTime;
    private long lastTime;
    private Map<BigDecimal, BigDecimal> bids = new HashMap<>();
    private Map<BigDecimal, BigDecimal> asks = new HashMap<>();
}
