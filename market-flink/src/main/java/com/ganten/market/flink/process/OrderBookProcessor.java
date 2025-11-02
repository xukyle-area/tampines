package com.ganten.market.flink.process;

import java.math.BigDecimal;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import com.ganten.market.common.enums.Action;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.input.Order;
import com.ganten.market.common.flink.output.OrderBook;

public class OrderBookProcessor extends KeyedProcessFunction<Long, Order, OrderBook> {

    private final int resolution;

    public OrderBookProcessor(int resolution) {
        this.resolution = resolution;
    }

    private MapState<BigDecimal, BigDecimal> bidState;
    private MapState<BigDecimal, BigDecimal> askState;

    @Override
    public void open(Configuration parameters) throws Exception {
        bidState = getRuntimeContext()
                .getMapState(new MapStateDescriptor<>(Side.BID.name(), BigDecimal.class, BigDecimal.class));
        askState = getRuntimeContext()
                .getMapState(new MapStateDescriptor<>(Side.ASK.name(), BigDecimal.class, BigDecimal.class));
    }

    @Override
    public void processElement(Order order, Context ctx, Collector<OrderBook> out) throws Exception {
        MapState<BigDecimal, BigDecimal> sideState = Side.BID.name().equals(order.getSide()) ? bidState : askState;

        BigDecimal price = order.getPrice();
        BigDecimal quantity = order.getQuantity();
        BigDecimal currentQuantity = sideState.get(price);

        System.out.println(
                "Processing order: " + order.getAction() + " " + order.getSide() + " " + price + " qty:" + quantity);

        if (Action.INSERT.name().equals(order.getAction())) {
            BigDecimal newQuantity = currentQuantity != null ? currentQuantity.add(quantity) : quantity;
            sideState.put(price, newQuantity);
            System.out.println("INSERT: " + order.getSide() + " " + price + " -> " + newQuantity);
        } else if (Action.DELETE.name().equals(order.getAction())) {
            if (currentQuantity != null) {
                BigDecimal newQuantity = currentQuantity.subtract(quantity);
                if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                    sideState.remove(price);
                    System.out.println("DELETE: " + order.getSide() + " " + price + " removed");
                } else {
                    sideState.put(price, newQuantity);
                    System.out.println("DELETE: " + order.getSide() + " " + price + " -> " + newQuantity);
                }
            }
        }

        // 注册定时器，每秒触发一次
        long nextTimer = ctx.timerService().currentProcessingTime() / 1000 * 1000 + 1000;
        ctx.timerService().registerProcessingTimeTimer(nextTimer);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<OrderBook> out) throws Exception {
        // 构建并输出订单簿
        OrderBook orderBook = new OrderBook();
        for (BigDecimal price : bidState.keys())
            orderBook.getBids().put(price, bidState.get(price));
        for (BigDecimal price : askState.keys())
            orderBook.getAsks().put(price, askState.get(price));
        orderBook.setContractId(ctx.getCurrentKey());

        System.out.println("Timer triggered for contract " + ctx.getCurrentKey() + ", Bids: "
                + orderBook.getBids().size() + ", Asks: " + orderBook.getAsks().size());

        out.collect(orderBook);

        // 注册下一个定时器
        ctx.timerService().registerProcessingTimeTimer(timestamp + 1000);
    }
}
