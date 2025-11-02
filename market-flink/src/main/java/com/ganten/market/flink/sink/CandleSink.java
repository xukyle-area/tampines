package com.ganten.market.flink.sink;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.output.Candle;
import com.ganten.market.flink.process.CandleProcessor;

public class CandleSink extends AbstractSink<Candle> {
    private static final long serialVersionUID = -5013366160879801184L;

    private final int resolution;

    public CandleSink(int resolution) {
        this.resolution = resolution;
    }

    /**
     * Tuple2<Long, CandleData> is calculated from CandleCalculator
     *
     * @see CandleProcessor
     */
    @Override
    public void invoke(Candle value, Context context) {
        long contractId = value.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateCandle(Market.GANTEN, contract, value, resolution);
    }
}
