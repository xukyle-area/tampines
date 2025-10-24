package com.ganten.market.flink.sink;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.pojo.ResultEventHolder;

public class TickSink extends AbstractSink<ResultEventHolder> {
    private final Market market;
    private transient Counter errorCounter;
    private transient Counter healthyCounter;

    public TickSink(Market market) {
        this.market = market;
    }

    @Override
    public void open(Configuration parameters) {

    }

    @Override
    public void invoke(ResultEventHolder value, Context context) {
        long cur = System.currentTimeMillis();
        long contractId = value.getContractId();
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }
        String last24HPrice = null;
        try {
            log.debug("getCandles {} use {} ms", contract.getSymbol(), System.currentTimeMillis() - cur);
        } catch (Exception e) {
            log.error(String.format("tick sink error, symbol: %s, className: %s, msg: %s", contract.getSymbol(),
                    e.getClass().getName(), e.getMessage()), e);
            errorCounter.inc();
        }
        writers.updateQuote(value.getTick(), last24HPrice, market, value.getContractId());
        healthyCounter.inc();
    }
}
