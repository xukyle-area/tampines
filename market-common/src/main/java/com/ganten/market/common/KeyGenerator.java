package com.ganten.market.common;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.enums.Market;
import com.google.common.base.Joiner;

public class KeyGenerator {

    private static final String REALTIME = "realtime";

    public static String realtimeKey(Market market, Contract contract) {
        return Joiner.on(SEPARATOR).join(REALTIME, market, contract);
    }

    private static final String CANDLE = "candle";

    public static String candleKey(Market market, Contract contract, int resolution) {
        return Joiner.on(SEPARATOR).join(CANDLE, market, contract, resolution);
    }

    private static final String TRADE = "trade";

    public static String tradeKey(Market market, Contract contract) {
        return Joiner.on(SEPARATOR).join(TRADE, market, contract);
    }

    private static final String ORDERBOOK = "order_book";

    public static String orderBookKey(Market market, Contract contract, Side side) {
        return Joiner.on(SEPARATOR).join(ORDERBOOK, market, contract, side);
    }

    private static final String TICKER = "ticker";

    public static String tickerKey(Market market, Contract contract) {
        return Joiner.on(SEPARATOR).join(TICKER, market, contract);
    }


    private static final String QUOTE24_PREFIX = "quote24h";
    private static final String ASK_PREFIX = "order_book_ask_2";
    private static final String BID_PREFIX = "order_book_bid_2";
    private static final String DIFF_PREFIX = "quote_diff_order_book";
    public static final String UPDATE_ID = "upid";
    public static final String DIFF_ASK_FIELD = "ask_";
    public static final String DIFF_BID_FIELD = "bid_";
    private static final String CANDLE_CACHE_PREFIX = "candle_cache";
    private static final String TRADE_CACHE_PREFIX = "trade_cache";
    private static final String HISTORY_INDEX_PRICE_PREFIX = "qip";
    private static final String SEPARATOR = "_";



    public static String generateRedisQuote24HKey(Contract contract, Market market) {
        return Joiner.on(SEPARATOR).join(QUOTE24_PREFIX, contract, market.toString());
    }

    public static String generateOrderBookAskKey(Contract contract, Market market) {
        return Joiner.on(SEPARATOR).join(ASK_PREFIX, contract, market.toString());
    }

    public static String generateOrderBookBidKey(Contract contract, Market market) {
        return Joiner.on(SEPARATOR).join(BID_PREFIX, contract, market.toString());
    }

    public static String generateDiffOrderBookKey(Contract contract, Market market) {
        return Joiner.on(SEPARATOR).join(DIFF_PREFIX, contract, market.toString());
    }

    public static String generateCandleCacheKey(Contract contract, int resolution) {
        return Joiner.on(SEPARATOR).join(CANDLE_CACHE_PREFIX, contract, resolution);
    }

    public static String generateTradeCacheKey(Contract contract) {
        return Joiner.on(SEPARATOR).join(TRADE_CACHE_PREFIX, contract);
    }

    public static String generateHistoryIndexPriceKey(String symbol, long timestamp) {
        return Joiner.on(SEPARATOR).join(HISTORY_INDEX_PRICE_PREFIX, symbol, timestamp);
    }

}
