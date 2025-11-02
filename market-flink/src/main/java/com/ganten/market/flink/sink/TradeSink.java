package com.ganten.market.flink.sink;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.flink.input.Trade;

public class TradeSink extends AbstractSink<Trade> {

    @Override
    public void invoke(Trade tradeInfo, Context context) {
        long contractId = tradeInfo.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateTrade(Market.GANTEN, contract, tradeInfo);
    }
}
