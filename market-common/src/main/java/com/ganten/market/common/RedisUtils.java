package com.ganten.market.common;

import com.ganten.market.common.pojo.Market;
import com.google.common.base.Joiner;

public class RedisUtils {

    private static final String QUOTE_PREFIX = "quote";
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

    public static String generateRedisQuoteKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(QUOTE_PREFIX, contractId, market.toString());
    }

    public static String generateRedisQuote24HKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(QUOTE24_PREFIX, contractId, market.toString());
    }

    public static String generateOrderBookAskKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(ASK_PREFIX, contractId, market.toString());
    }

    public static String generateOrderBookBidKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(BID_PREFIX, contractId, market.toString());
    }

    public static String generateDiffOrderBookKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(DIFF_PREFIX, contractId, market.toString());
    }

    public static String generateCandleCacheKey(long contractId, int resolution) {
        return Joiner.on(SEPARATOR).join(CANDLE_CACHE_PREFIX, contractId, resolution);
    }

    public static String generateTradeCacheKey(long contractId) {
        return Joiner.on(SEPARATOR).join(TRADE_CACHE_PREFIX, contractId);
    }

    public static String generateHistoryIndexPriceKey(String symbol, long timestamp) {
        return Joiner.on(SEPARATOR).join(HISTORY_INDEX_PRICE_PREFIX, symbol, timestamp);
    }

}
