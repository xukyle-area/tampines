package com.ganten.market.flink.sink;


import com.ganten.market.common.pojo.Market;
import com.ganten.market.flink.model.DiffOrderbookEvent;

public class DiffOrderBookSink extends AbstractSink<DiffOrderbookEvent> {

    private final Market market;

    public DiffOrderBookSink(Market market) {
        this.market = market;
    }

    @Override
    public void invoke(DiffOrderbookEvent event, Context context) {
        writers.updateDiffOrderBook(event, market);
    }
}
