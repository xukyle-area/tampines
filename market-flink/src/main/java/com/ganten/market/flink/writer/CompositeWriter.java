package com.ganten.market.flink.writer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.*;

public class CompositeWriter implements BaseWriter {

    private static final Logger log = LoggerFactory.getLogger(CompositeWriter.class);

    private final List<BaseWriter> writers;

    public CompositeWriter(Map<String, String> mapConf) {
        MqttWriter mqttWriter = new MqttWriter(mapConf);
        RedisWriter redisWriter = new RedisWriter(mapConf);
        this.writers = Stream.of(mqttWriter, redisWriter).collect(Collectors.toList());
    }

    public void updateSingleTrade(TradeInfo tradeInfo, long contractId) {
        write(w -> w.updateTrade(tradeInfo, contractId), "updateSingleTrade");
    }

    public void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {
        write(w -> w.updateQuote(tick, last24HPrice, market, contractId), "updateQuote");
    }

    public void updateOrderBook(OrderBook orderBook, Market market, long contractId) {
        write(w -> w.updateOrderBook(orderBook, market, contractId), "updateOrderBook");
    }

    public void updateTrade(TradeInfo tradeInfo, long contractId) {
        write(w -> w.updateTrade(tradeInfo, contractId), "updateTrade");
    }

    public void updateCandle(CandleData candleData, long contractId, int resolution) {
        write(w -> w.updateCandle(candleData, contractId, resolution), "updateCandle");
    }

    private void write(Consumer<BaseWriter> consumer, String name) {
        long s = System.currentTimeMillis();
        for (BaseWriter w : writers) {
            try {
                consumer.accept(w);
            } catch (Exception e) {
                log.error(name + " error", e);
            }
        }
        log.info("{} use {} ms", name, System.currentTimeMillis() - s);
    }
}
