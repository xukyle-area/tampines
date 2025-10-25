package com.ganten.market.outer.writer;

import org.apache.commons.lang.StringUtils;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.model.RealTimeQuote;
import com.ganten.market.common.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisQuoteWriter {

    private static final String LAST = "last";
    private static final String BID = "bid";
    private static final String ASK = "ask";

    public static void updateRealTimeQuote(RealTimeQuote realTimeQuote) {
        try (Jedis jedis = RedisClient.getResource()) {
            final String key = KeyGenerator.realtimeKey(realTimeQuote.getMarket(), realTimeQuote.getContract());
            jedis.hset(key, LAST, realTimeQuote.getLast());
            if (StringUtils.isNotEmpty(realTimeQuote.getAsk())) {
                jedis.hset(key, ASK, realTimeQuote.getAsk());
            }
            if (StringUtils.isNotEmpty(realTimeQuote.getBid())) {
                jedis.hset(key, BID, realTimeQuote.getBid());
            }
        }
    }
}
