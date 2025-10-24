package com.ganten.market.flink.sink;

import org.apache.flink.configuration.Configuration;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.pojo.ResultEventHolder;

public class OrderBookSink extends AbstractSink<ResultEventHolder> {

    private final Market market;

    public OrderBookSink(Market market) {
        this.market = market;
    }

    @Override
    public void open(Configuration parameters) {
        super.open(parameters);
    }

    @Override
    public void invoke(ResultEventHolder value, Context context) {
        writers.updateOrderBook(value.getOrderBook(), market, value.getContractId());
    }
}
