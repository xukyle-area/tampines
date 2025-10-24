package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.OrderBookData;
import com.ganten.market.common.pojo.OrderBook;
import com.ganten.market.common.pojo.OrderBookTuple;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.common.pojo.ResultEventType;
import com.ganten.market.flink.model.DiffOrderbookEvent;
import com.ganten.market.flink.utils.DecimalUtils;

public class DiffOrderbookCalculator extends ProcessFunction<List<ResultEventHolder>, DiffOrderbookEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DiffOrderbookCalculator.class);

    private final Map<String, String> parameterTool;

    public DiffOrderbookCalculator(Map<String, String> parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void processElement(List<ResultEventHolder> elements,
            ProcessFunction<List<ResultEventHolder>, DiffOrderbookEvent>.Context ctx,
            Collector<DiffOrderbookEvent> out) {
        if (elements.isEmpty()) {
            return;
        }
        long contractId = elements.get(0).getContractId();
        Contract contract = Contract.getContractById(contractId);
        if (contract == null) {
            return;
        }
        int tickSize = DecimalUtils.getScale(new BigDecimal(contract.getTickSize()));
        int lotSize = DecimalUtils.getScale(new BigDecimal(contract.getLotSize()));
        long subDiffElementsLastId = 0L;
        List<ResultEventHolder> subDiffElementList = new ArrayList<>();
        for (ResultEventHolder element : elements) {
            logger.debug("difforderbook element [{}]", element);
            OrderBook orderBook = element.getOrderBook();
            // divide DIFFORDERBOOKALL element
            if (element.getResult_event_type() == ResultEventType.RESULT_EVENT_TYPE_DIFFORDERBOOKALL) {
                subDiffElements(out, contractId, tickSize, lotSize, subDiffElementList);
                out.collect(buildDiffAllEvent(element, contractId, tickSize, lotSize));
                subDiffElementsLastId = 0L;
                continue;
            }
            // divide discontinuous element
            if (subDiffElementsLastId != 0L && subDiffElementsLastId + 1 != orderBook.getUpdateId()) {
                subDiffElements(out, contractId, tickSize, lotSize, subDiffElementList);
            }
            subDiffElementList.add(element);
            subDiffElementsLastId = orderBook.getUpdateId();
        }
        subDiffElements(out, contractId, tickSize, lotSize, subDiffElementList);
    }

    private void subDiffElements(Collector<DiffOrderbookEvent> out, long contractId, int tickSize, int lotSize,
            List<ResultEventHolder> subDiffElementList) {
        if (!subDiffElementList.isEmpty()) {
            out.collect(buildDiffEvent(subDiffElementList, contractId, tickSize, lotSize));
            subDiffElementList.clear();
        }
    }

    private DiffOrderbookEvent buildDiffEvent(List<ResultEventHolder> subDiffElementList, Long contractId, int tickSize,
            int lotSize) {
        if (subDiffElementList.isEmpty()) {
            return null;
        }
        DiffOrderbookEvent diffOrderbookEvent = new DiffOrderbookEvent();
        diffOrderbookEvent.setContractId(contractId);
        diffOrderbookEvent.setEventType(ResultEventType.RESULT_EVENT_TYPE_DIFFORDERBOOK);
        for (int i = 0; i < subDiffElementList.size(); i++) {
            OrderBook orderBook = subDiffElementList.get(i).getOrderBook();
            if (i == 0) {
                diffOrderbookEvent.setFirstId(orderBook.getUpdateId());
            }
            diffOrderbookEvent.setLastId(orderBook.getUpdateId());
            for (OrderBookTuple tuple : orderBook.getAsks()) {
                OrderBookData orderBookData = toOrderBookData(tuple, tickSize, lotSize);
                diffOrderbookEvent.getAskMap().put(orderBookData.getPrice(), orderBookData);
            }
            for (OrderBookTuple ob : orderBook.getBids()) {
                OrderBookData orderBookData = toOrderBookData(ob, tickSize, lotSize);
                diffOrderbookEvent.getBidMap().put(orderBookData.getPrice(), orderBookData);
            }
        }
        return diffOrderbookEvent;
    }

    private OrderBookData toOrderBookData(OrderBookTuple tuple, int tickSize, int lotSize) {
        OrderBookData res = new OrderBookData();
        tuple.getQuantity().setScale(lotSize, RoundingMode.HALF_EVEN);
        res.setQuantity(tuple.getQuantity());
        tuple.getPrice().setScale(tickSize, RoundingMode.HALF_EVEN);
        res.setPrice(tuple.getPrice());
        return res;
    }

    private DiffOrderbookEvent buildDiffAllEvent(ResultEventHolder diffElement, Long contractId, int tickSize,
            int lotSize) {
        DiffOrderbookEvent diffOrderbookEvent = new DiffOrderbookEvent();
        diffOrderbookEvent.setContractId(contractId);
        diffOrderbookEvent.setEventType(ResultEventType.RESULT_EVENT_TYPE_DIFFORDERBOOKALL);
        OrderBook orderBook = diffElement.getOrderBook();
        diffOrderbookEvent.setUpdateId(orderBook.getUpdateId());
        for (OrderBookTuple tuple : orderBook.getAsks()) {
            OrderBookData orderBookData = toOrderBookData(tuple, tickSize, lotSize);
            diffOrderbookEvent.getAskMap().put(orderBookData.getPrice(), orderBookData);
        }
        for (OrderBookTuple ob : orderBook.getBids()) {
            OrderBookData orderBookData = toOrderBookData(ob, tickSize, lotSize);
            diffOrderbookEvent.getBidMap().put(orderBookData.getPrice(), orderBookData);
        }
        return diffOrderbookEvent;
    }


}
