package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.util.Comparator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import com.ganten.market.common.flink.OrderBook;
import com.ganten.market.common.flink.Tick;
import com.ganten.market.common.model.PriceQuantity;

public class BestQuoteProcess extends ProcessFunction<OrderBook, Tick> {

    @Override
    public void processElement(OrderBook orderBook, Context ctx, Collector<Tick> out) {
        if (orderBook == null || orderBook.getBids().isEmpty() || orderBook.getAsks().isEmpty()) {
            return;
        }

        BigDecimal bestBid = orderBook.getBids().stream().max(Comparator.comparing(PriceQuantity::getPrice))
                .map(PriceQuantity::getPrice).orElse(BigDecimal.ZERO);

        BigDecimal bestAsk = orderBook.getAsks().stream().min(Comparator.comparing(PriceQuantity::getPrice))
                .map(PriceQuantity::getPrice).orElse(BigDecimal.ZERO);

        Tick quote = new Tick();
        quote.setContractId(orderBook.getContractId());
        quote.setBid(bestBid);
        quote.setAsk(bestAsk);
        quote.setTimestamp(orderBook.getTimestamp());

        out.collect(quote);
    }
}
