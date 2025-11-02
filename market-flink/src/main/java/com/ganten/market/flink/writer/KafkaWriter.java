package com.ganten.market.flink.writer;

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
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.common.flink.output.OrderBook;
import com.ganten.market.common.flink.output.Tick;
import com.ganten.market.common.model.PublishMessage;
import com.ganten.market.common.utils.JsonUtils;

/**
 * send message to kafka, use to send mqtt message to client
 */
public class KafkaWriter implements BaseWriter {

    private static final Logger log = LoggerFactory.getLogger(KafkaWriter.class);

    private static final String KAFKA_TOPIC = "api";
    private static final String TICK_TOPIC = "mqtt/quote/%s/tick";
    private static final String TRADE_TOPIC = "mqtt/quote/%s/trade";
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
    public void updateOrderBook(Market market, Contract contract, double grouping, OrderBook orderBook) {
        if (contract == null) {
            return;
        }
        String mqttTopic = String.format("mqtt/quote/%s/orderBook/?&grouping=%s", contract.getSymbol(), grouping);
        log.info(
                "KafkaWriter updateOrderBook - market: {}, grouping: {}, contract: {}, orderBook.market: {}, orderBook.grouping: {}, orderBook.contractId: {}",
                market, grouping, contract, orderBook.getMarket(), orderBook.getGrouping(), orderBook.getContractId());
        this.sendKafkaMessage(mqttTopic, orderBook);
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
        publishMessage.setPayload(JsonUtils.toJson(msg));

        log.info("do send {}: {}", mqttTopic, publishMessage.getPayload());
        producer.send(new ProducerRecord<>(KAFKA_TOPIC, JsonUtils.toJson(publishMessage)), ((metadata, ex) -> {
            if (ex != null) {
                log.error("send mqtt error", ex);
            }
        }));
    }
}
