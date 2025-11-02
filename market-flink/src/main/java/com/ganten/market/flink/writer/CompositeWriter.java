package com.ganten.market.flink.writer;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.common.flink.output.OrderBook;
import com.ganten.market.common.flink.output.Tick;

public class CompositeWriter implements BaseWriter {

    private static final Logger log = LoggerFactory.getLogger(CompositeWriter.class);

    private static final String UPDATE_TICK = "updateTick";
    private static final String UPDATE_ORDER_BOOK = "updateOrderBook";
    private static final String UPDATE_TRADE = "updateTrade";
    private static final String UPDATE_CANDLE = "updateCandle";


    private final List<BaseWriter> writers;

    public CompositeWriter() {
        KafkaWriter mqttWriter = new KafkaWriter();
        RedisWriter redisWriter = new RedisWriter();
        this.writers = Stream.of(mqttWriter, redisWriter).collect(Collectors.toList());
    }

    public void updateTick(Market market, Contract contract, Tick tick) {
        this.write(w -> w.updateTick(market, contract, tick), UPDATE_TICK);
    }

    public void updateOrderBook(Market market, Contract contract, double grouping, OrderBook orderBook) {
        this.write(w -> w.updateOrderBook(market, contract, grouping, orderBook), UPDATE_ORDER_BOOK);
    }

    public void updateTrade(Market market, Contract contract, Trade tradeInfo) {
        this.write(w -> w.updateTrade(market, contract, tradeInfo), UPDATE_TRADE);
    }

    public void updateCandle(Market market, Contract contract, Candle candleData, int resolution) {
        this.write(w -> w.updateCandle(market, contract, candleData, resolution), UPDATE_CANDLE);
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
