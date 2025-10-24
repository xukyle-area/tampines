package com.ganten.market.common.model;

import org.jetbrains.annotations.NotNull;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.pojo.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RealTimeQuote {
    private long timestamp;
    private Contract contract;
    @NotNull
    private Market market;
    @NotNull
    private String last;
    private String ask;
    private String bid;
}

