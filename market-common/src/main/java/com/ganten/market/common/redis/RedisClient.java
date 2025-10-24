package com.ganten.market.common.redis;

import java.time.Duration;
import org.apache.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 单机版 Redis 连接池单例
 */
@Slf4j
public class RedisClient {

    // 静态单例池
    private static volatile JedisPool jedisPool;

    private RedisClient() {
        // 禁止外部实例化
    }

    /**
     * 初始化 Redis 连接池（只调用一次）
     */
    public static void init(String host, int port, String password) {
        if (jedisPool != null) {
            return;
        }

        synchronized (RedisClient.class) {
            if (jedisPool != null) {
                return;
            }

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(50); // 最大连接数
            poolConfig.setMaxIdle(20); // 最大空闲连接
            poolConfig.setMinIdle(2); // 最小空闲连接
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinEvictableIdleTimeMillis(Duration.ofMinutes(1).toMillis());
            poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
            poolConfig.setNumTestsPerEvictionRun(3);

            if (StringUtils.isNotBlank(password)) {
                jedisPool = new JedisPool(poolConfig, host, port, 5000, password);
                log.info("Initialized Redis pool (with password) at {}:{}", host, port);
            } else {
                jedisPool = new JedisPool(poolConfig, host, port, 5000);
                log.info("Initialized Redis pool (no password) at {}:{}", host, port);
            }

            // 连接测试
            try (Jedis jedis = jedisPool.getResource()) {
                String pong = jedis.ping();
                log.info("Redis connection test success: {}", pong);
            } catch (Exception e) {
                log.error("Failed to connect/authenticate to Redis at {}:{}: {}", host, port, e.getMessage(), e);
                throw new RuntimeException("Failed to initialize Redis connection pool", e);
            }
        }
    }

    /**
     * 获取 Jedis 实例（自动从池中取）
     */
    public static Jedis getResource() {
        if (jedisPool == null) {
            throw new IllegalStateException("RedisClient not initialized. Call init() first.");
        }
        return jedisPool.getResource();
    }

    /**
     * 安全关闭连接池
     */
    public static void shutdown() {
        if (jedisPool != null) {
            synchronized (RedisClient.class) {
                if (jedisPool != null) {
                    jedisPool.close();
                    jedisPool = null;
                    log.info("Redis pool shutdown complete.");
                }
            }
        }
    }
}
