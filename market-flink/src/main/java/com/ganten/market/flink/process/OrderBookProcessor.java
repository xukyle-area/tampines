package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import com.ganten.market.common.enums.Action;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.input.Order;
import com.ganten.market.common.flink.output.OrderBook;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderBookProcessor extends KeyedProcessFunction<Long, Order, OrderBook> {

    private final int resolution;
    private double grouping;


    /**
     * resolution 的取值为: 1,5,10,100
     * 含义为几倍的 tickSize
     * @param resolution
     */
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
        Contract contract = Contract.getContractById(order.getContractId());
        this.grouping = contract.getTickSize() * resolution;
        MapState<BigDecimal, BigDecimal> sideState = Side.BID.name().equals(order.getSide()) ? bidState : askState;

        BigDecimal price = order.getPrice();
        BigDecimal groupedPrice = this.groupPrice(price, this.grouping);
        BigDecimal quantity = order.getQuantity();
        BigDecimal currentQuantity = sideState.get(groupedPrice);

        log.info("Processing order: {} {} {} (grouped: {}) qty: {}", order.getAction(), order.getSide(), price,
                groupedPrice, quantity);

        if (Action.INSERT.name().equals(order.getAction())) {
            BigDecimal newQuantity = currentQuantity != null ? currentQuantity.add(quantity) : quantity;
            sideState.put(groupedPrice, newQuantity);
            log.info("INSERT: {} {} -> {}", order.getSide(), groupedPrice, newQuantity);
        } else if (Action.DELETE.name().equals(order.getAction())) {
            if (currentQuantity != null) {
                BigDecimal newQuantity = currentQuantity.subtract(quantity);
                if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                    sideState.remove(groupedPrice);
                    log.info("DELETE: {} {} removed", order.getSide(), groupedPrice);
                } else {
                    sideState.put(groupedPrice, newQuantity);
                    log.info("DELETE: {} {} -> {}", order.getSide(), groupedPrice, newQuantity);
                }
            }
        }

        // 注册定时器，每秒触发一次
        long nextTimer = ctx.timerService().currentProcessingTime() / 1000 * 1000 + 1000;
        ctx.timerService().registerProcessingTimeTimer(nextTimer);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<OrderBook> out) throws Exception {
        // 构建并输出订单簿，直接使用分组后的价格
        OrderBook orderBook = new OrderBook();

        // 直接使用 MapState 中的分组后数据
        for (java.util.Map.Entry<BigDecimal, BigDecimal> entry : bidState.entries()) {
            orderBook.getBids().put(entry.getKey(), entry.getValue());
        }
        for (java.util.Map.Entry<BigDecimal, BigDecimal> entry : askState.entries()) {
            orderBook.getAsks().put(entry.getKey(), entry.getValue());
        }
        orderBook.setMarket(Market.GANTEN);
        orderBook.setContractId(ctx.getCurrentKey());

        log.info("Timer triggered for contract {}, Bids: {}, Asks: {}", ctx.getCurrentKey(), orderBook.getBids().size(),
                orderBook.getAsks().size());

        out.collect(orderBook);

        // 注册下一个定时器
        ctx.timerService().registerProcessingTimeTimer(timestamp + 1000);
    }

    /**
     * 根据grouping对价格进行分组
     */
    private BigDecimal groupPrice(BigDecimal price, double grouping) {
        if (grouping == 0.0) {
            return price;
        }
        BigDecimal group = BigDecimal.valueOf(grouping);
        return price.divide(group, 0, RoundingMode.FLOOR).multiply(group);
    }
}
