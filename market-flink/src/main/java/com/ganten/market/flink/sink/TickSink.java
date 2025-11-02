package com.ganten.market.flink.sink;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.output.Tick;

/**
 * Tick 数据 Sink，将 Tick 写入外部存储（由 CompositeWriter 决定）
 */
public class TickSink extends AbstractSink<Tick> {
    @Override
    public void invoke(Tick tick, Context context) {
        long contractId = tick.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateTick(Market.GANTEN, contract, tick);
    }
}
