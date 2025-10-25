package com.ganten.market.flink.writer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.pojo.*;
import com.google.common.base.Joiner;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisWriter implements BaseWriter {

    private static final String QUOTE_PREFIX = "quote";
    private static final String SEPARATOR = "_";
    private static final long CACHE_MIN_TIME = 1641830400000L;
    private static final int CACHE_POINTS_NUM = 1502;
    private static final long TRADE_CACHE_TIME = 2 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000;

    private final JedisCluster jedis;

    public RedisWriter(Map<String, String> parameterTool) {
        final String redisHosts = parameterTool.get("redis.hosts");
        final Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String redisHost : redisHosts.split(",")) {
            jedisClusterNodes.add(HostAndPort.from(redisHost));
        }
        this.jedis = new JedisCluster(jedisClusterNodes);
    }

    @Override
    public void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {
        final String key = Joiner.on(SEPARATOR).join(QUOTE_PREFIX, contractId, market.toString());
        final Map<String, String> map = new HashMap<>();
        map.put("ask", tick.getAsk().toString());
        map.put("bid", tick.getBid().toString());
        map.put("last", tick.getLast().toString());
        if (last24HPrice != null && !"".equals(tick.getLast().toString())) {
            BigDecimal last = new BigDecimal(tick.getLast().toString());
            BigDecimal first = new BigDecimal(last24HPrice);
            BigDecimal change = last.subtract(first);
            BigDecimal changePercent = change.divide(first, 8, RoundingMode.DOWN).multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
            map.put("change24h", change.toString());
            map.put("changePercent24h", changePercent.toString());
        }

        jedis.hset(key, map);
    }

    @Override
    public void updateOrderBook(OrderBook orderBook, Market market, long contractId) {
        final String askKey = KeyGenerator.generateOrderBookAskKey(contractId, market);
        final String bidKey = KeyGenerator.generateOrderBookBidKey(contractId, market);

        save2Redis(askKey, orderBook.getAsks());
        save2Redis(bidKey, orderBook.getBids());
    }

    private void save2Redis(String key, List<OrderBookTuple> list) {
        Map<String, String> originMap = jedis.hgetAll(key);
        Map<String, String> newMap = price2Quantity(list);
        doSave2Redis(key, originMap, newMap, list.size());
    }

    private void doSave2Redis(String key, Map<String, String> originMap, Map<String, String> newMap, int size) {
        String[] price2Del = originMap.keySet().stream().filter(t -> !newMap.containsKey(t)).toArray(String[]::new);
        if (size > 0) {
            for (String price : price2Del) {
                newMap.put(price, "0");
            }
            jedis.hset(key, newMap);
        }
        if (price2Del.length > 0) {
            jedis.hdel(key, price2Del);
        }
    }

    private Map<String, String> price2Quantity(List<OrderBookTuple> list) {
        Map<String, String> res = new HashMap<>();
        for (OrderBookTuple t : list) {
            res.put(t.getPrice().toString(), t.getQuantity().toString());
        }
        return res;
    }

    @Override
    public void updateCandle(CandleData candleData, long contractId, int resolution) {
        Contract contract = Contract.getContractById(contractId);
        byte[] key = KeyGenerator.candleKey(contract, resolution).getBytes(StandardCharsets.UTF_8);
        double startTime = Double.parseDouble(candleData.getStartTime());
        jedis.zadd(key, startTime, candleData.toByteArray());
        jedis.zremrangeByScore(key, CACHE_MIN_TIME, startTime - resolution * 1000L * CACHE_POINTS_NUM);
    }

    @Override
    public void updateTrade(TradeInfo tradeInfo, long contractId) {
        byte[] key = KeyGenerator.generateTradeCacheKey(contractId).getBytes(StandardCharsets.UTF_8);
        jedis.zadd(key, tradeInfo.getTime(), tradeInfo.toByteArray());
        jedis.zremrangeByScore(key, CACHE_MIN_TIME, System.currentTimeMillis() - TRADE_CACHE_TIME);
    }

    @Override
    public List<TradeInfo> getTrade(long contractId, long startTime, long endTime) {
        byte[] key = KeyGenerator.generateTradeCacheKey(contractId).getBytes(StandardCharsets.UTF_8);
        List<TradeInfo> trades = new ArrayList<>();
        for (byte[] trade : jedis.zrangeByScore(key, startTime, endTime)) {
            trades.add(TradeInfo.parseFrom(trade));
        }
        return trades;
    }
}
