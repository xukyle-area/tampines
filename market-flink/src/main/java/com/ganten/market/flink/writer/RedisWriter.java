package com.ganten.market.flink.writer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.Candle;
import com.ganten.market.common.flink.OrderBook;
import com.ganten.market.common.flink.Tick;
import com.ganten.market.common.flink.Trade;
import com.ganten.market.common.model.PriceQuantity;
import com.ganten.market.common.redis.RedisClient;
import com.ganten.market.common.utils.ObjectUtils;
import redis.clients.jedis.Jedis;

public class RedisWriter implements BaseWriter {

    private static final long CACHE_MIN_TIME = 1641830400000L;
    private static final int CACHE_POINTS_NUM = 1502;
    private static final long TRADE_CACHE_TIME = 2 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000;

    @Override
    public void updateTick(Market market, Contract contract, Tick tick) {
        String tickKey = KeyGenerator.tickKey(market, contract);

        try (Jedis jedis = RedisClient.getResource()) {
            jedis.hset(tickKey, ObjectUtils.toStringMap(tick));
        }
    }

    @Override
    public void updateOrderBook(Market market, Contract contract, OrderBook orderBook) {
        final String askKey = KeyGenerator.orderBookKey(market, contract, Side.ASK);
        final String bidKey = KeyGenerator.orderBookKey(market, contract, Side.BID);

        this.saveOrderBook(askKey, orderBook.getAsks());
        this.saveOrderBook(bidKey, orderBook.getBids());
    }

    private void saveOrderBook(String key, List<PriceQuantity> list) {
        try (Jedis jedis = RedisClient.getResource()) {
            Map<String, String> originMap = jedis.hgetAll(key);

            Map<String, String> newMap = list.stream().collect(Collectors.toMap(o -> o.getPrice().toString(),
                    o -> o.getQuantity().toString(), (existing, replacement) -> {
                        BigDecimal e = new BigDecimal(existing);
                        BigDecimal r = new BigDecimal(replacement);
                        return e.add(r).toString();
                    }));
            originMap.keySet().removeIf(o -> !newMap.containsKey(o));
            jedis.hset(key, newMap);
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
