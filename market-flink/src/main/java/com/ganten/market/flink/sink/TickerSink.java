package com.ganten.market.flink.sink;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.output.Ticker;

/**
 * Ticker 数据 Sink，将 Ticker 写入外部存储（由 CompositeWriter 决定）
 */
public class TickerSink extends AbstractSink<Ticker> {
    @Override
    public void invoke(Ticker tick, Context context) {
        long contractId = tick.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateTicker(Market.GANTEN, contract, tick);
    }
}
