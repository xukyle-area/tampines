package com.ganten.market.flink.sink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.CandleData;

public final class CandleSink extends AbstractSink<Tuple2<Long, CandleData>> {
    private static final Logger logger = LoggerFactory.getLogger(CandleSink.class);
    private static final long serialVersionUID = -5013366160879801184L;

    private final int resolution;

    public CandleSink(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public void invoke(Tuple2<Long, CandleData> value, Context context) {
        logger.info("sink the candle data {}", value);
        final long contractId = value.f0;
        final CandleData candleData = value.f1;
        compositeWriter.updateCandle(candleData, contractId, resolution);
    }
}
