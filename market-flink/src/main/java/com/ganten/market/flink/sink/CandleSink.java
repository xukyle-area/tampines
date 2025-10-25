package com.ganten.market.flink.sink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.Candle;
import com.ganten.market.flink.process.CandleCalculator;

public class CandleSink extends AbstractSink<Candle> {
    private static final Logger logger = LoggerFactory.getLogger(CandleSink.class);
    private static final long serialVersionUID = -5013366160879801184L;

    private final int resolution;

    public CandleSink(int resolution) {
        this.resolution = resolution;
    }

    /**
     * Tuple2<Long, CandleData> is calculated from CandleCalculator
     *
     * @see CandleCalculator
     */
    @Override
    public void invoke(Candle value, Context context) {
        long contractId = value.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateCandle(Market.GANTEN, contract, value, resolution);
    }
}
