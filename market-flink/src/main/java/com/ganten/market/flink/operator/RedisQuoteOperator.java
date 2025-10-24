package com.ganten.market.flink.operator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.model.OrderBookData;
import com.ganten.market.common.pojo.*;
import com.ganten.market.flink.model.DiffOrderbookEvent;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisQuoteOperator implements QuoteOperator {

    private static final Logger log = LoggerFactory.getLogger(QuoteOperator.class);

    private static final String QUOTE_PREFIX = "quote";
    private static final String QUOTE24_PREFIX = "quote24h";
    private static final String SEPARATOR = "_";
    private static final long CACHE_MIN_TIME = 1641830400000L;
    private static final int CACHE_POINTS_NUM = 1502;
    private static final long TRADE_CACHE_TIME = 2 * 24 * 60 * 60 * 1000 + 10 * 60 * 1000;

    private static final long SEC_24H = 24 * 60 * 60;

    private final JedisCluster jedis;

    private final Map<Long, Long> localId = new ConcurrentHashMap<>();


    public RedisQuoteOperator(Map<String, String> parameterTool) {
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
    public void update24HQuote(Last24HData data, Market market, long contractId) {
        final String key = Joiner.on(SEPARATOR).join(QUOTE24_PREFIX, contractId, market.toString());
        final Map<String, String> map = new HashMap<>();
        map.put("max24h", data.getMax());
        map.put("min24h", data.getMin());
        map.put("vol24h", data.getVol());

        jedis.hset(key, map);
        jedis.expire(key, SEC_24H);
    }

    @Override
    public void updateOrderBook(OrderBook orderBook, Market market, long contractId) {
        final String askKey = KeyGenerator.generateOrderBookAskKey(contractId, market);
        final String bidKey = KeyGenerator.generateOrderBookBidKey(contractId, market);

        save2Redis(askKey, orderBook.getAsks());
        save2Redis(bidKey, orderBook.getBids());
    }

    @Override
    public void updateDiffOrderBook(DiffOrderbookEvent event, Market market) {
        Long contractId = event.getContractId();
        if (!checkUpdateId(event)) {
            log.error("redis id error, {}", event);
            return;
        }
        final String key = KeyGenerator.generateDiffOrderBookKey(contractId, market);
        if (event.getEventType() == ResultEventType.DIFFORDERBOOKALL) {
            Map<String, String> originMap = jedis.hgetAll(key);
            Result result = reconcile(originMap, event);
            if (result.isDiff()) {
                Map<String, String> updateMap = result.getDiffs().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getNewValue()));
                jedis.hset(key, updateMap);
                log.error("update diff orderbook all.diffs : {}", result.getDiffs());
            }
            List<String> deleteKeys = buildDeleteOrderBookKeys(originMap, result.getDiffs());
            if (!deleteKeys.isEmpty()) {
                jedis.hdel(key, deleteKeys.toArray(new String[0]));
            }
            localId.put(contractId, event.getUpdateId());
        } else {
            Map<String, String> newMap = new HashMap<>();
            event.getAskMap().forEach(
                    (price, ob) -> newMap.put(KeyGenerator.DIFF_ASK_FIELD + price, ob.getQuantity().toString()));
            event.getBidMap().forEach(
                    (price, ob) -> newMap.put(KeyGenerator.DIFF_BID_FIELD + price, ob.getQuantity().toString()));
            newMap.put(KeyGenerator.UPDATE_ID, event.getLastId() + "");
            jedis.hset(key, newMap);
            localId.put(contractId, event.getLastId());
        }
        log.debug("contract:{}, event:{}.", contractId, event);
    }

    private List<String> buildDeleteOrderBookKeys(Map<String, String> originMap, Map<String, Result.Pair> diffs) {
        Stream<String> newZeroKey = diffs.entrySet().stream()
                .filter(entry -> new BigDecimal(entry.getValue().getNewValue()).compareTo(BigDecimal.ZERO) == 0)
                .map(Map.Entry::getKey);
        Stream<String> originZeroKey = originMap.entrySet().stream().filter(entry -> !diffs.containsKey(entry.getKey()))
                .filter(entry -> new BigDecimal(entry.getValue()).compareTo(BigDecimal.ZERO) == 0)
                .map(Map.Entry::getKey);
        return Stream.concat(newZeroKey, originZeroKey).collect(Collectors.toList());
    }

    private Result reconcile(Map<String, String> originMap, DiffOrderbookEvent event) {
        Map<String, Result.Pair> diffs = new HashMap<>();
        if (!originMap.containsKey(KeyGenerator.UPDATE_ID)
                || !originMap.get(KeyGenerator.UPDATE_ID).equals(event.getUpdateId() + "")) {
            log.error("diff orderbook id error.{}", event.getUpdateId());
            diffs.put(KeyGenerator.UPDATE_ID,
                    new Result.Pair(originMap.get(KeyGenerator.UPDATE_ID), event.getUpdateId().toString()));
        }
        diffs.putAll(reconcileOrderBook(KeyGenerator.DIFF_ASK_FIELD, originMap, event.getAskMap()));
        diffs.putAll(reconcileOrderBook(KeyGenerator.DIFF_BID_FIELD, originMap, event.getBidMap()));
        return new Result(diffs);
    }

    private Map<String, Result.Pair> reconcileOrderBook(String hashKeyPrefix, Map<String, String> originMap,

            NavigableMap<BigDecimal, OrderBookData> newMap) {
        Map<String, BigDecimal> origins =
                originMap.entrySet().stream().filter(entry -> entry.getKey().contains(hashKeyPrefix))
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new BigDecimal(entry.getValue())));
        Map<String, BigDecimal> news = newMap.entrySet().stream().collect(
                Collectors.toMap(entry -> hashKeyPrefix + entry.getKey(), entry -> entry.getValue().getQuantity()));
        Set<String> keys = Sets.union(origins.keySet(), news.keySet());

        Map<String, Result.Pair> diffs = new HashMap<>();
        keys.forEach(key -> {
            BigDecimal originValue = origins.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal newValue = news.getOrDefault(key, BigDecimal.ZERO);
            if (originValue.compareTo(newValue) != 0) {
                diffs.put(key, new Result.Pair(originValue.toString(), newValue.toString()));
            }
        });
        return diffs;
    }

    @AllArgsConstructor
    private static class Result {

        public boolean isDiff() {
            return diffs != null && !diffs.isEmpty();
        }

        @Getter
        private Map<String, Pair> diffs;

        @Data
        private static class Pair {

            private final String originValue;

            private final String newValue;
        }
    }

    private boolean checkUpdateId(DiffOrderbookEvent event) {
        Long contractId = event.getContractId();
        if (event.getEventType() == ResultEventType.DIFFORDERBOOKALL) {
            return event.getUpdateId() >= localId.getOrDefault(contractId, 1L);
        }
        return event.getFirstId() <= localId.getOrDefault(contractId, 1L) + 1
                && event.getLastId() >= localId.getOrDefault(contractId, 1L) + 1;
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
        byte[] key = KeyGenerator.generateCandleCacheKey(contractId, resolution).getBytes(StandardCharsets.UTF_8);
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
