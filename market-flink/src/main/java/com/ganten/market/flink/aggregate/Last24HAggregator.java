package com.ganten.market.flink.aggregate;

import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.AggregateFunction;
import com.ganten.market.common.model.TradeData;


public class Last24HAggregator implements AggregateFunction<TradeData, List<TradeData>, List<TradeData>> {

    private static final long serialVersionUID = 640172640815377032L;

    @Override
    public List<TradeData> createAccumulator() {
        return new ArrayList<>();
    }

    @Override
    public List<TradeData> add(TradeData value, List<TradeData> accumulator) {
        accumulator.add(value);
        return accumulator;
    }

    @Override
    public List<TradeData> getResult(List<TradeData> accumulator) {
        return accumulator;
    }

    @Override
    public List<TradeData> merge(List<TradeData> a, List<TradeData> b) {
        a.addAll(b);
        return a;
    }

}
