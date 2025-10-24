package com.ganten.market.common.model;

import org.jetbrains.annotations.NotNull;
import com.ganten.market.common.pojo.Market;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DayHistoryQuote {
    private long timestamp;
    private long contractId;
    @NotNull
    private Market market;
    @NotNull
    private String preClose;
}
