package com.ganten.market.flink.writer;


import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.common.flink.output.OrderBook;
import com.ganten.market.common.flink.output.Ticker;

public interface BaseWriter {

    default void updateTicker(Market market, Contract contract, Ticker tick) {}

    default void updateOrderBook(Market market, Contract contract, double grouping, OrderBook orderBook) {}

    default void updateTrade(Market market, Contract contract, Trade tradeInfo) {}

    default public void updateCandle(Market market, Contract contract, Candle candleData, int resolution) {}
}
