package com.ganten.market.outter.writer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import com.ganten.market.common.RedisUtils;
import com.ganten.market.common.model.RealTimeQuote;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.commands.JedisCommands;

@Slf4j
public class RedisQuoteWriter implements QuoteWriter {

    private static final String LAST = "last";

    private final JedisCommands jedis;

    public static RedisQuoteWriter of(String clusterConfig, String password) {
        return new RedisQuoteWriter(getJedis(clusterConfig, password));
    }

    public static JedisCommands getJedis(String clusterConfig, String password) {
        final Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String redisHost : clusterConfig.split(",")) {
            jedisClusterNodes.add(HostAndPort.from(redisHost));
        }

        if (jedisClusterNodes.size() == 1) {
            // 单机模式
            HostAndPort hostAndPort = jedisClusterNodes.iterator().next();
            Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
            if (!Objects.isNull(password) && !password.isEmpty()) {
                jedis.auth(password);
            }
            log.info("Using single Redis instance at {}:{}", hostAndPort.getHost(), hostAndPort.getPort());
            return jedis;
        } else {
            // 集群模式
            if (Objects.isNull(password) || password.isEmpty()) {
                log.info("Redis password is empty, skipping password authentication.");
                return new JedisCluster(jedisClusterNodes);
            }
            return new JedisCluster(jedisClusterNodes, null, password);
        }
    }

    public RedisQuoteWriter(JedisCommands jedis) {
        this.jedis = jedis;
    }

    @Override
    public void updateRealTimeQuote(RealTimeQuote realTimeQuote) {
        final String key = RedisUtils.generateRedisQuoteKey(realTimeQuote.getContractId(), realTimeQuote.getMarket());
        jedis.hset(key, LAST, realTimeQuote.getLast());
        if (StringUtils.isNotEmpty(realTimeQuote.getAsk())) {
            jedis.hset(key, "ask", realTimeQuote.getAsk());
        }
        if (StringUtils.isNotEmpty(realTimeQuote.getBid())) {
            jedis.hset(key, "bid", realTimeQuote.getBid());
        }
    }
}
