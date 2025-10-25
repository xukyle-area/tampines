package com.ganten.market.common.model;

import org.jetbrains.annotations.NotNull;
import com.ganten.market.common.enums.Market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DayHistoryQuote {
    private long timestamp;
    private long contractId;
    @NotNull
    private Market market;
    @NotNull
    private String preClose;
}
