package com.ganten.market.common.utils;

public class SymbolUtils {

    public static String toSymbol(String base, String quote) {
        return base + "." + quote;
    }

    public static String[] toCurrencies(String symbol) {
        return symbol.split("\\.");
    }

    public static String removeDot(String symbol) {
        return symbol.replaceAll("\\.", "");
    }
}
