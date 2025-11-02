package com.ganten.market.outer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.ganten.market.common.redis.RedisClient;

@EnableScheduling
@SpringBootApplication
public class MarketOuterApplication {

    public static void main(String[] args) {
        RedisClient.init("localhost", 6379, "");
        SpringApplication.run(MarketOuterApplication.class, args);
    }
}
