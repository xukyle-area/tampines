package com.ganten.market.common.enums;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.ganten.market.common.model.CurrencyPair;
import lombok.Getter;

@Getter
public enum Contract {
    BTC_USD(1L, "BTC", "USD", "BTC/USD", "0.01", "1"),
    ETH_USD(2L, "ETH", "USD", "ETH/USD", "0.01", "1"),
    LTC_USD(3L,"LTC", "USD", "LTC/USD", "0.01","1"),
    XRP_USD(4L, "XRP", "USD", "XRP/USD", "0.01", "1"),
    BCH_USD(5L, "BCH", "USD", "BCH/USD", "0.01", "1");

    private Long id;
    private String base;
    private String quote;
    private String symbol;
    private String tickSize;
    private String lotSize;

    Contract(Long id, String base, String quote, String symbol, String tickSize, String lotSize) {
        this.id = id;
        this.base = base;
        this.quote = quote;
        this.symbol = symbol;
        this.tickSize = tickSize;
        this.lotSize = lotSize;
    }

    public static Contract getContractById(Long id) {
        for (Contract contract : Contract.values()) {
            if (contract.getId().equals(id)) {
                return contract;
            }
        }
        return null;
    }

    public static Contract fromString(String name) {
        for (Contract contract : Contract.values()) {
            if (contract.name().equalsIgnoreCase(name)) {
                return contract;
            }
        }
        return null;
    }

    public static Long getContractIdBySymbol(String symbol) {
        for (Contract contract : Contract.values()) {
            if (contract.getSymbol().equalsIgnoreCase(symbol)) {
                return contract.getId();
            }
        }
        return null;
    }


    public static Contract getContractBySymbol(String symbol) {
        for (Contract contract : Contract.values()) {
            if (contract.getSymbol().equalsIgnoreCase(symbol)) {
                return contract;
            }
        }
        return null;
    }

    public List<String> getGrouping() {
        return Stream.of(1, 5, 10, 100).map(t -> new BigDecimal(t).multiply(new BigDecimal(tickSize)))
                .map(BigDecimal::stripTrailingZeros).map(BigDecimal::toPlainString).collect(Collectors.toList());
    }

    public CurrencyPair toCurrencyPair() {
        return new CurrencyPair(base, quote);
    }
}
