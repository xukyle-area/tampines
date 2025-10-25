package com.ganten.market.flink.writer;

import java.util.List;
import com.ganten.market.common.pojo.*;

public interface BaseWriter {

    /**
     * for tick job
     * update redis and mqtt
     */
    default void updateQuote(Tick tick, String last24HPrice, Market market, long contractId) {}

    /**
    * for orderbook job
    * update redis and mqtt
    */
    default void updateOrderBook(OrderBook orderBook, Market market, long contractId) {}


    /**
     * for trade job
     * update redis and mqtt
     */
    default void updateTrade(TradeInfo tradeInfo, long contractId) {}

    /**
     * for trade job
     * update dynamodb
     * no implementation
     */
    default void updateTrade(List<ResultEventHolder> value) {}

    default void updateCandle(CandleData candleData, long contractId, int resolution) {}

    default List<TradeInfo> getTrade(long contractId, long startTime, long endTime) {
        return null;
    }

}
