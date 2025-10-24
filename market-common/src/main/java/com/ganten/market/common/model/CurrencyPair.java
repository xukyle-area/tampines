package com.ganten.market.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CurrencyPair {
    private String base;
    private String quote;

    public String getSymbol() {
        return base + quote;
    }

    public String getSymbolWithDot() {
        return base + "." + quote;
    }

    public String getSymbolWithSubline() {
        return base + "_" + quote;
    }
}
