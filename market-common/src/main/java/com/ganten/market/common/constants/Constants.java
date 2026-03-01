package com.ganten.market.common.constants;

public class Constants {
    public final static String KAFKA_SOURCE = "kafka-source";
    public final static String CRYPTO_COM_SUBSCRIBE = "subscribe";
    public final static String CRYPTO_CHANNELS = "channels";

    public final static int ONE = 1;
    public final static int THREE = 3;
    public final static int FIVE = 5;
    public final static int TEN = 10;
    public final static int TWENTY = 20;
    public final static int SIXTY = 60;
    public final static int NINETY = 90;
    public final static int THOUSAND = 1000;
    public final static int FIVE_THOUSAND = 5000;

    // 从环境变量读取，提供默认值
    public final static String BOOTSTRAP_SERVERS = getEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    public final static String REDIS_HOST = getEnv("REDIS_HOST", "localhost");
    public final static int REDIS_PORT = Integer.parseInt(getEnv("REDIS_PORT", "6379"));
    public final static String REDIS_PASSWORD = getEnv("REDIS_PASSWORD", "");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
}
