package com.ganten.market.flink.operator;

import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.*;
import com.ganten.market.flink.model.DiffOrderbookEvent;

public class AllOperators implements QuoteOperator {

    private static final Logger log = LoggerFactory.getLogger(AllOperators.class);

    private final List<QuoteOperator> writers;

    public AllOperators(List<QuoteOperator> writers) {
        if (writers == null || writers.isEmpty()) {
            throw new RuntimeException("writers is empty");
        }
        this.writers = writers;
    }

    public void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {
        write(w -> w.updateQuote(tick, last24HPrice, market, contractId), "updateQuote");
    }

    public void updateOrderBook(OrderBook orderBook, Market market, long contractId) {
        write(w -> w.updateOrderBook(orderBook, market, contractId), "updateOrderBook");
    }

    public void updateDiffOrderBook(DiffOrderbookEvent event, Market market) {
        write(w -> w.updateDiffOrderBook(event, market), "updateOrderBook");
    }

    public void update24HQuote(Last24HData data, Market market, long contractId) {
        write(w -> w.update24HQuote(data, market, contractId), "update24HQuote");
    }

    public void updateTrade(TradeInfo tradeInfo, long contractId) {
        write(w -> w.updateTrade(tradeInfo, contractId), "updateTrade");
    }

    public void updateCandle(CandleData candleData, long contractId, int resolution) {
        write(w -> w.updateCandle(candleData, contractId, resolution), "updateCandle");
    }

    private void write(Consumer<QuoteOperator> consumer, String name) {
        long s = System.currentTimeMillis();
        for (QuoteOperator w : writers) {
            try {
                consumer.accept(w);
            } catch (Exception e) {
                log.error(name + " error", e);
            }
        }
        log.info("{} use {} ms", name, System.currentTimeMillis() - s);
    }
}
