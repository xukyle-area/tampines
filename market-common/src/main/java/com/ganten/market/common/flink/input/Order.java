package com.ganten.market.common.flink.input;

import java.math.BigDecimal;
import com.ganten.market.common.flink.BaseObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseObject {
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String side;
    private String action;
}
