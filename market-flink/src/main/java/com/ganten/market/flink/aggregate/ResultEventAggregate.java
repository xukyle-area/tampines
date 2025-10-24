package com.ganten.market.flink.aggregate;

import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.AggregateFunction;
import com.ganten.market.common.pojo.ResultEventHolder;

public class ResultEventAggregate
        implements AggregateFunction<ResultEventHolder, List<ResultEventHolder>, List<ResultEventHolder>> {
    @Override
    public List<ResultEventHolder> createAccumulator() {
        return new ArrayList<>();
    }

    @Override
    public List<ResultEventHolder> add(ResultEventHolder value, List<ResultEventHolder> accumulator) {
        accumulator.add(value);
        return accumulator;
    }

    @Override
    public List<ResultEventHolder> getResult(List<ResultEventHolder> accumulator) {
        return accumulator;
    }

    @Override
    public List<ResultEventHolder> merge(List<ResultEventHolder> a, List<ResultEventHolder> b) {
        a.addAll(b);
        return a;
    }
}
