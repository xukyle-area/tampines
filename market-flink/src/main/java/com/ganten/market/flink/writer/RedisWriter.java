package com.ganten.market.flink.writer;

import java.nio.charset.StandardCharsets;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.common.flink.output.OrderBook;
import com.ganten.market.common.flink.output.Tick;
import com.ganten.market.common.redis.RedisClient;
import com.ganten.market.common.utils.ObjectUtils;
import redis.clients.jedis.Jedis;

public class RedisWriter implements BaseWriter {

    private static final long CACHE_MIN_TIME = 1641830400000L;
    private static final int CACHE_POINTS_NUM = 1502;
    private static final long TRADE_CACHE_TIME = 2 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000;

    @Override
    public void updateTick(Market market, Contract contract, Tick tick) {
        String tickKey = KeyGenerator.tickerKey(market, contract);

        try (Jedis jedis = RedisClient.getResource()) {
            jedis.hset(tickKey, ObjectUtils.toStringMap(tick));
        }
    }

    @Override
    public void updateOrderBook(Market market, Contract contract, OrderBook orderBook) {
        final String askKey = KeyGenerator.orderBookKey(market, contract, Side.ASK);
        final String bidKey = KeyGenerator.orderBookKey(market, contract, Side.BID);

        try (Jedis jedis = RedisClient.getResource()) {
            // 存储买单（BID）
            if (orderBook.getBids() != null && !orderBook.getBids().isEmpty()) {
                jedis.del(bidKey); // 先清空
                for (java.util.Map.Entry<java.math.BigDecimal, java.math.BigDecimal> entry : orderBook.getBids()
                        .entrySet()) {
                    jedis.hset(bidKey, entry.getKey().toString(), entry.getValue().toString());
                }
            }

            // 存储卖单（ASK）
            if (orderBook.getAsks() != null && !orderBook.getAsks().isEmpty()) {
                jedis.del(askKey); // 先清空
                for (java.util.Map.Entry<java.math.BigDecimal, java.math.BigDecimal> entry : orderBook.getAsks()
                        .entrySet()) {
                    jedis.hset(askKey, entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }
    }

    @Override
    public void updateCandle(Market market, Contract contract, Candle candleData, int resolution) {
        byte[] key = KeyGenerator.candleKey(market, contract, resolution).getBytes(StandardCharsets.UTF_8);
        double startTime = Double.parseDouble(candleData.getStartTime());
        try (Jedis jedis = RedisClient.getResource()) {
            jedis.zadd(key, startTime, candleData.toByteArray());
            jedis.zremrangeByScore(key, CACHE_MIN_TIME, startTime - resolution * 1000L * CACHE_POINTS_NUM);
        }
    }

    @Override
    public void updateTrade(Market market, Contract contract, Trade tradeInfo) {
        byte[] key = KeyGenerator.tradeKey(market, contract).getBytes(StandardCharsets.UTF_8);
        try (Jedis jedis = RedisClient.getResource()) {
            jedis.zadd(key, tradeInfo.getTime(), tradeInfo.toByteArray());
            jedis.zremrangeByScore(key, CACHE_MIN_TIME, System.currentTimeMillis() - TRADE_CACHE_TIME);
        }
    }
}
