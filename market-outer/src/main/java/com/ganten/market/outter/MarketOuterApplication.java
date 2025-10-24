package com.ganten.market.outter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketOuterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketOuterApplication.class, args);
    }
}
