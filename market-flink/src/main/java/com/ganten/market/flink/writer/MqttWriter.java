package com.ganten.market.flink.writer;

import static com.ganten.market.common.constants.Constants.*;
import static java.util.stream.Collectors.toList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.OrderBookData;
import com.ganten.market.common.pojo.*;
import com.ganten.market.common.utils.JsonUtils;
import com.ganten.market.common.utils.SerializationUtils;
import com.ganten.market.flink.model.*;
import com.ganten.market.flink.utils.DecimalUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.Value;

public class MqttWriter implements BaseWriter {

    private static final Logger log = LoggerFactory.getLogger(MqttWriter.class);

    private final Producer<String, String> producer;
    private final Integer depth;

    public MqttWriter(Map<String, String> parameterTool) {

        final String orderBookDepth = parameterTool.get("orderbook.depth");
        depth = orderBookDepth == null ? 10 : Integer.parseInt(orderBookDepth);

        final String servers = parameterTool.get("kafka.bootstrap.servers");
        final Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", servers);
        producer = new KafkaProducer<String, String>(properties, new StringSerializer(), new StringSerializer());
    }

    @Override
    public void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }
        TickMsg msg = new TickMsg();
        int tickSize = DecimalUtils.getScale(new BigDecimal(contract.getTickSize()));
        msg.setSymbol(contract.getSymbol());

        msg.setLast(tick.getLast().toString());
        msg.setAsk(tick.getAsk().toString());
        msg.setBid(tick.getBid().toString());

        if (last24HPrice != null && tick.getLast() != null) {
            BigDecimal last = tick.getLast();
            BigDecimal first = new BigDecimal(last24HPrice);
            BigDecimal change = last.subtract(first);
            BigDecimal changePercent = change.divide(first, 8, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_EVEN);
            msg.setChange24hours(change.setScale(tickSize, RoundingMode.HALF_EVEN).toPlainString());
            msg.setChangePercent24hours(changePercent.toPlainString());
        }

        this.sendMqtt(String.format(TICK_TOPIC, contract.getSymbol()), msg);
    }

    @Override
    public void updateOrderBook(OrderBook orderBook, Market market, long contractId) {
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }
        int tickSize = DecimalUtils.getScale(new BigDecimal(contract.getTickSize()));
        int lotSize = DecimalUtils.getScale(new BigDecimal(contract.getLotSize()));
        List<OrderBookData> askList =
                orderBook.getAsks().stream().map(ob -> toOrderBookData(ob, tickSize, lotSize)).collect(toList());
        List<OrderBookData> bidList =
                orderBook.getBids().stream().map(ob -> toOrderBookData(ob, tickSize, lotSize)).collect(toList());
        List<String> groupingList = contract.getGrouping();
        String symbol = contract.getSymbol();
        for (String grouping : groupingList) {
            OrderBookResponse response = SerializationUtils.buildOrderBook(askList, bidList, grouping);
            OrderBookMsg msg = buildMsg(symbol, grouping, response);
            this.sendMqtt(String.format(ORDER_BOOK_TOPIC, symbol, grouping), msg);
        }
    }

    @Override
    public void updateTrade(TradeInfo tradeInfo, long contractId) {
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }

        TradeMsg msg = new TradeMsg();
        int tickSize = DecimalUtils.getScale(new BigDecimal(contract.getTickSize()));
        int lotSize = DecimalUtils.getScale(new BigDecimal(contract.getLotSize()));
        msg.setSymbol(contract.getSymbol());
        msg.setTimestamp(tradeInfo.getTime());
        msg.setId(tradeInfo.getId());
        msg.setTime(tradeInfo.getTime());
        msg.setPrice(tradeInfo.getPrice().setScale(tickSize).toString());
        msg.setVolume(tradeInfo.getVolume().setScale(lotSize).toString());
        msg.setSide(tradeInfo.isBuyerMaker() ? "bid" : "ask");

        this.sendMqtt(String.format(TRADE_TOPIC, contract.getSymbol()), msg);
    }

    @Override
    public void updateCandle(CandleData candleData, long contractId, int resolution) {
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }
        CandleMsg msg = new CandleMsg();
        msg.setSymbol(contract.getSymbol());
        msg.setResolution(resolution);
        msg.setStartTime(Long.parseLong(candleData.getStartTime()));
        msg.setOpen(candleData.getOpen());
        msg.setClose(candleData.getClose());
        msg.setHigh(candleData.getHigh());
        msg.setLow(candleData.getLow());
        msg.setVolume(candleData.getVolume());

        this.sendMqtt(String.format(CANDLE_TOPIC, contract.getSymbol(), resolution), msg);
    }

    private void sendMqtt(String mqttTopic, MqttMsg msg) {
        doSend(mqttTopic, msg);
    }

    private void doSend(String mqttTopic, MqttMsg msg) {
        log.info("doSend {}: {}", mqttTopic, JsonUtils.toJson(msg));
        PublishMessage publishMessage = new PublishMessage();
        publishMessage.setMqttTopic(mqttTopic);
        publishMessage.setTimestamp(System.currentTimeMillis());
        publishMessage.setPayload(ByteString.copyFrom(JsonUtils.toJson(msg), StandardCharsets.UTF_8).toByteArray());

        producer.send(new ProducerRecord<>(TOPIC, JsonUtils.toJson(publishMessage)), ((metadata, ex) -> {
            if (ex != null) {
                log.error("send mqtt error", ex);
            }
        }));
    }

    private OrderBookData toOrderBookData(OrderBookTuple tuple, int tickSize, int lotSize) {
        OrderBookData res = new OrderBookData();
        res.setQuantity(tuple.getQuantity().setScale(lotSize, RoundingMode.HALF_EVEN));
        res.setPrice(tuple.getPrice().setScale(tickSize, RoundingMode.HALF_EVEN));
        return res;
    }

    private List<String> toOrderBookVO(Value value) {
        List<String> res = new ArrayList<>();
        res.add(value.getListValue().getValues(0).getStringValue());
        res.add(value.getListValue().getValues(1).getStringValue());
        return res;
    }

    private OrderBookMsg buildMsg(String symbol, String grouping, OrderBookResponse response) {
        OrderBookMsg msg = new OrderBookMsg();
        msg.setGrouping(grouping);
        msg.setSymbol(symbol);
        msg.setAsks(response.getAsks().stream().limit(depth).map(this::toOrderBookVO).collect(toList()));
        msg.setBids(response.getBids().stream().limit(depth).map(this::toOrderBookVO).collect(toList()));
        return msg;
    }
}
