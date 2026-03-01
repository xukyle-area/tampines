package com.ganten.market.flink.writer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.common.flink.output.OrderBook;
import com.ganten.market.common.flink.output.Ticker;
import com.ganten.market.common.redis.RedisClient;
import com.ganten.market.common.utils.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisWriter implements BaseWriter {

    private static final long CACHE_MIN_TIME = 1641830400000L;
    private static final int CACHE_POINTS_NUM = 1502;
    private static final long TRADE_CACHE_TIME = 2 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000;

    public RedisWriter() {
        // 从 Constants 读取配置（Constants 会从环境变量加载）
        RedisClient.init(Constants.REDIS_HOST, Constants.REDIS_PORT, Constants.REDIS_PASSWORD);
    }

    @Override
    public void updateTicker(Market market, Contract contract, Ticker ticker) {
        String tickerKey = KeyGenerator.tickerKey(market, contract);

        try (Jedis jedis = RedisClient.getResource()) {
            jedis.hset(tickerKey, ObjectUtils.toStringMap(ticker));
        }
    }

    @Override
    public void updateOrderBook(Market market, Contract contract, double grouping, OrderBook orderBook) {
        final String askKey = KeyGenerator.orderBookKey(market, contract, Side.ASK, grouping);
        final String bidKey = KeyGenerator.orderBookKey(market, contract, Side.BID, grouping);

        try (Jedis jedis = RedisClient.getResource()) {
            // 使用 Pipeline 批量执行，减少数据不一致窗口
            Pipeline pipeline = jedis.pipelined();

            // 存储买单（BID）
            if (orderBook.getBids() != null && !orderBook.getBids().isEmpty()) {
                pipeline.del(bidKey);
                Map<String, String> bidMap = new HashMap<>();
                for (Map.Entry<java.math.BigDecimal, java.math.BigDecimal> entry : orderBook.getBids().entrySet()) {
                    bidMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
                pipeline.hset(bidKey, bidMap);
            }

            // 存储卖单（ASK）
            if (orderBook.getAsks() != null && !orderBook.getAsks().isEmpty()) {
                pipeline.del(askKey);
                Map<String, String> askMap = new HashMap<>();
                for (Map.Entry<java.math.BigDecimal, java.math.BigDecimal> entry : orderBook.getAsks().entrySet()) {
                    askMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
                pipeline.hset(askKey, askMap);
            }

            // 批量执行所有命令
            pipeline.sync();
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
