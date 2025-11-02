package com.ganten.market.flink.sink;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.flink.output.OrderBook;

public class OrderBookSink extends AbstractSink<OrderBook> {
    @Override
    public void invoke(OrderBook orderBook, Context context) {
        long contractId = orderBook.getContractId();
        Contract contract = Contract.getContractById(contractId);
        compositeWriter.updateOrderBook(orderBook.getMarket(), contract, orderBook);
    }
}
