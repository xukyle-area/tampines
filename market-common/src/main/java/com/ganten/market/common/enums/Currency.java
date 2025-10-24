package com.ganten.market.common.enums;

public enum Currency {
    USD("USD", false), HKD("HKD", false), BTC("BTC", true), ETH("ETH", true), LTC("LTC", true), XRP("XRP",
            true), BCH("BCH", true);

    private final String code;
    private final boolean isCrypto;

    public static Currency getCurrencyByCode(String code) {
        for (Currency currency : Currency.values()) {
            if (currency.getCode().equalsIgnoreCase(code)) {
                return currency;
            }
        }
        return null;
    }

    Currency(String code, boolean isCrypto) {
        this.code = code;
        this.isCrypto = isCrypto;
    }

    public String getCode() {
        return code;
    }

    public boolean isCrypto() {
        return isCrypto;
    }
}
