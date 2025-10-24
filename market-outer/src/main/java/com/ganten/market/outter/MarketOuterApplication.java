package com.ganten.market.outter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.ganten.market.common.redis.RedisClient;

@EnableScheduling
@SpringBootApplication
public class MarketOuterApplication {


    public static void main(String[] args) {
        RedisClient.init("127.0.0.1", 6379, "redispassword");
        SpringApplication.run(MarketOuterApplication.class, args);
    }
}
