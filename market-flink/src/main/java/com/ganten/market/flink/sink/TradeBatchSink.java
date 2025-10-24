package com.ganten.market.flink.sink;

import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.operator.QuoteOperator;
import com.google.common.collect.Lists;

public class TradeBatchSink extends RichSinkFunction<List<ResultEventHolder>> {

    private QuoteOperator quoteWriter;

    private static final int batchSize = 25;

    @Override
    public void open(Configuration parameters) throws Exception {}

    @Override
    public void invoke(List<ResultEventHolder> value, Context context) throws Exception {
        List<List<ResultEventHolder>> partition = Lists.partition(value, batchSize);
        for (List<ResultEventHolder> part : partition) {
            quoteWriter.updateTrade(part);
        }
    }
}
