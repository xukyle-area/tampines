package com.ganten.market.flink.writer;


import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.Candle;
import com.ganten.market.common.flink.OrderBook;
import com.ganten.market.common.flink.Tick;
import com.ganten.market.common.flink.Trade;

public interface BaseWriter {

    /**
     * 在不同的 job 中进行写入
     * @link OrderbookJob : ask | bid
     * @link TradeJob: last | volume
     * @link TickJob: highest24hours | lowest24hours | change24hours | changePercent24hours
     */
    default void updateTick(Market market, Contract contract, Tick tick) {}

    default void updateOrderBook(Market market, Contract contract, OrderBook orderBook) {}

    default void updateTrade(Market market, Contract contract, Trade tradeInfo) {}

    default public void updateCandle(Market market, Contract contract, Candle candleData, int resolution) {}
}
