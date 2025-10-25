package com.ganten.market.flink.writer;

import static java.util.stream.Collectors.toList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.Candle;
import com.ganten.market.common.flink.OrderBook;
import com.ganten.market.common.flink.Tick;
import com.ganten.market.common.flink.Trade;
import com.ganten.market.common.model.PriceQuantity;
import com.ganten.market.common.model.PublishMessage;
import com.ganten.market.common.utils.BigDecimalUtils;
import com.ganten.market.common.utils.JsonUtils;
import com.google.protobuf.ByteString;

/**
 * send message to kafka, use to send mqtt message to client
 */
public class KafkaWriter implements BaseWriter {

    private static final Logger log = LoggerFactory.getLogger(KafkaWriter.class);

    private static final String KAFKA_TOPIC = "mqtt";
    private static final String TICK_TOPIC = "mqtt/quote/%s/tick";
    private static final String TRADE_TOPIC = "mqtt/quote/%s/trade";
    private static final String ORDER_BOOK_TOPIC = "mqtt/quote/%s/orderBook/?&grouping=%s";
    private static final String CANDLE_TOPIC = "mqtt/quote/%s/candle/?resolution=%s";

    private final Producer<String, String> producer;

    public KafkaWriter() {
        final Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", Constants.BOOTSTRAP_SERVERS);
        producer = new KafkaProducer<String, String>(properties, new StringSerializer(), new StringSerializer());
    }

    @Override
    public void updateTick(Market market, Contract contract, Tick tick) {
        this.sendKafkaMessage(String.format(TICK_TOPIC, contract), tick);
    }

    @Override
    public void updateOrderBook(Market market, Contract contract, OrderBook orderBook) {
        int tickSize = BigDecimalUtils.getScale(new BigDecimal(contract.getTickSize()));
        int lotSize = BigDecimalUtils.getScale(new BigDecimal(contract.getLotSize()));
        List<PriceQuantity> askList =
                orderBook.getAsks().stream().map(ob -> this.toOrderBookData(ob, tickSize, lotSize)).collect(toList());
        List<PriceQuantity> bidList =
                orderBook.getBids().stream().map(ob -> this.toOrderBookData(ob, tickSize, lotSize)).collect(toList());
        List<String> groupingList = contract.getGrouping();
        for (String grouping : groupingList) {
            OrderBook response = OrderBook.build(askList, bidList, grouping);
            String mqttTopic = this.buildMqttTopic(ORDER_BOOK_TOPIC, contract, grouping);
            this.sendKafkaMessage(mqttTopic, response);
        }
    }

    @Override
    public void updateTrade(Market market, Contract contract, Trade tradeInfo) {
        if (contract == null) {
            return;
        }
        String mqttTopic = this.buildMqttTopic(TRADE_TOPIC, contract);
        this.sendKafkaMessage(mqttTopic, tradeInfo);
    }

    @Override
    public void updateCandle(Market market, Contract contract, Candle candleData, int resolution) {
        if (contract == null) {
            return;
        }

        String mqttTopic = this.buildMqttTopic(CANDLE_TOPIC, contract, resolution);
        this.sendKafkaMessage(mqttTopic, candleData);
    }

    private String buildMqttTopic(String pattern, Contract contract, Object... args) {
        return String.format(pattern, args);
    }

    private void sendKafkaMessage(String mqttTopic, Object msg) {
        PublishMessage publishMessage = new PublishMessage();
        publishMessage.setMqttTopic(mqttTopic);
        publishMessage.setTimestamp(System.currentTimeMillis());
        publishMessage.setPayload(ByteString.copyFrom(JsonUtils.toJson(msg), StandardCharsets.UTF_8).toByteArray());

        log.info("do send {}: {}", mqttTopic, JsonUtils.toJson(msg));
        producer.send(new ProducerRecord<>(KAFKA_TOPIC, JsonUtils.toJson(publishMessage)), ((metadata, ex) -> {
            if (ex != null) {
                log.error("send mqtt error", ex);
            }
        }));
    }

    private PriceQuantity toOrderBookData(PriceQuantity tuple, int tickSize, int lotSize) {
        PriceQuantity res = new PriceQuantity();
        res.setQuantity(tuple.getQuantity().setScale(lotSize, RoundingMode.HALF_EVEN));
        res.setPrice(tuple.getPrice().setScale(tickSize, RoundingMode.HALF_EVEN));
        return res;
    }
}
