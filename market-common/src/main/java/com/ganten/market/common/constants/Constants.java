package com.ganten.market.common.constants;

public class Constants {

    public static final String TRADE_REDIS_SINK = "trade_redis_sink";
    public static final String TRADE_MQTT_SINK = "trade_mqtt_sink";
    public static final String TRADE_DYNAMO_SINK = "trade_dynamo_sink";
    public static final String TRADE_DYNAMO_AGG = "trade_dynamo_agg";

    public static final String TOPIC = "api";
    public static final String TICK_TOPIC = "quote/%s/tick";
    public static final String TRADE_TOPIC = "quote/%s/trade";
    public static final String ORDER_BOOK_TOPIC = "quote/%s/orderBook/?&grouping=%s";
    public static final String CANDLE_TOPIC = "quote/%s/candle/?resolution=%s";


    public final static String BINANCE_SUBSCRIBE = "SUBSCRIBE";
    public final static String CRYPTO_COM_SUBSCRIBE = "subscribe";
    public final static String CRYPTO_CHANNELS = "channels";

    public static final String ACCESS_KEY = "quote.aws-s3.accessKey";
    public static final String SECRET_KEY = "quote.aws-s3.secretKey";
    public static final String REGION = "quote.aws-s3.region";
    public static final String BUCKET = "quote.aws-s3.bucket";

    public final static String APPLICATION_ZIP = "application/zip";
    public final static int ZERO = 0;

    public final static int ONE = 1;
    public final static String SPOT = "SPOT";

    public final static String KAFKA_SOURCE = "kafka-source";
    public final static String CANDLE_JOB = "candle_job";

    public final static String REDIS = "redis";
    public final static String MQTT = "mqtt";
}
