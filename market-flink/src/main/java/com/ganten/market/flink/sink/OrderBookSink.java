package com.ganten.market.flink.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Gauge;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.pojo.ResultEventHolder;

public class OrderBookSink extends AbstractSink<ResultEventHolder> {

    private final Market market;

    private long delay;

    public OrderBookSink(Market market) {
        this.market = market;
    }

    @Override
    public void open(Configuration parameters) {
        super.open(parameters);
        Gauge<Long> tradeDelayGauge = getRuntimeContext().getMetricGroup().gauge("orderbookDelay", () -> delay);
    }

    @Override
    public void invoke(ResultEventHolder value, Context context) {
        delay = System.currentTimeMillis() - value.getTimestamp();
        writers.updateOrderBook(value.getOrderBook(), market, value.getContractId());
    }
}
