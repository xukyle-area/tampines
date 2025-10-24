package com.ganten.market.flink.operator;

import java.util.List;
import com.ganten.market.common.pojo.*;
import com.ganten.market.flink.model.DiffOrderbookEvent;

public interface QuoteOperator {

    default void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {}

    default void updateOrderBook(OrderBook orderBook, Market market, long contractId) {}

    default void updateDiffOrderBook(DiffOrderbookEvent event, Market market) {}

    default void update24HQuote(Last24HData data, Market market, long contractId) {}

    default void updateTrade(TradeInfo tradeInfo, long contractId) {}

    default void updateTrade(List<ResultEventHolder> value) {}

    default void updateCandle(CandleData candleData, long contractId, int resolution) {}

    default List<TradeInfo> getTrade(long contractId, long startTime, long endTime) {
        return null;
    }

}
