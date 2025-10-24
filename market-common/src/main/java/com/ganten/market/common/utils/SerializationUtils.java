package com.ganten.market.common.utils;

import static java.util.stream.Collectors.toList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.ganten.market.common.model.OrderBookData;
import com.ganten.market.common.pojo.OrderBookResponse;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

public class SerializationUtils {

    private static final int MAX_SCALE = 14; // max tickSize scale 14 ,to stripTrailingZeros.

    public static OrderBookResponse buildOrderBook(List<OrderBookData> askList, List<OrderBookData> bidList,
            String tickSize) {

        OrderBookResponse orderBookResponse = new OrderBookResponse();
        orderBookResponse.setAsks(new ArrayList<>());
        orderBookResponse.setBids(new ArrayList<>());
        orderBookResponse.setTimestamp(System.currentTimeMillis());

        BigDecimal grouping;
        askList.sort((a, b) -> a.getPrice().compareTo(b.getPrice())); // 价格从低到高排序
        bidList.sort((a, b) -> b.getPrice().compareTo(a.getPrice())); // 价格从高到低排序
        try {
            grouping = new BigDecimal(tickSize);
        } catch (Exception e) {
            List<Value> asks =
                    askList.stream().map(t -> buildOrderBookValue(t.getPrice().toString(), t.getQuantity().toString()))
                            .collect(toList());
            List<Value> bids =
                    bidList.stream().map(t -> buildOrderBookValue(t.getPrice().toString(), t.getQuantity().toString()))
                            .collect(toList());
            orderBookResponse.getAsks().addAll(asks);
            orderBookResponse.getBids().addAll(bids);
            return orderBookResponse;
        }

        BigDecimal end = grouping; // 价格闭区间结束位置，初始区间为(0, grouping]
        BigDecimal sum = BigDecimal.ZERO; // 区间内所有价格累加数量
        int scale = grouping.setScale(MAX_SCALE, RoundingMode.HALF_EVEN).stripTrailingZeros().scale();
        for (OrderBookData vs : askList) { // ask为卖单，价格从低到高排列
            BigDecimal price = vs.getPrice();
            BigDecimal num = vs.getQuantity();
            if (price.compareTo(end) <= 0) {
                sum = sum.add(num);
                continue;
            }
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                orderBookResponse.getAsks().add(buildOrderBookValue(end, sum, scale));
            }
            end = grouping.multiply(price.divide(grouping, 0, RoundingMode.UP)); // 分段长度 * 分段数 向上取整,为当前价格所在分段区间的结束位置
            sum = num;
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            orderBookResponse.getAsks().add(buildOrderBookValue(end, sum, scale));
            sum = BigDecimal.ZERO;
        }

        end = BigDecimal.valueOf(Double.MAX_VALUE);
        for (OrderBookData vs : bidList) { // bids为卖单，价格从高到低排列
            BigDecimal price = vs.getPrice();
            BigDecimal num = vs.getQuantity();
            if (price.compareTo(end) >= 0) {
                sum = sum.add(num);
                continue;
            }
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                orderBookResponse.getBids().add(buildOrderBookValue(end, sum, scale));
            }
            end = grouping.multiply(price.divide(grouping, 0, RoundingMode.DOWN)); // 分段长度 * 分段数 ，区间为 [end, end +
                                                                                   // grouping)从右至左
            sum = num;
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            orderBookResponse.getBids().add(buildOrderBookValue(end, sum, scale));
        }
        return orderBookResponse;
    }

    private static Value buildOrderBookValue(String price, String num) {
        return Value.newBuilder()
                .setListValue(ListValue.newBuilder().addValues(Value.newBuilder().setStringValue(price).build())
                        .addValues(Value.newBuilder().setStringValue(num).build()).build())
                .build();
    }

    private static Value buildOrderBookValue(BigDecimal price, BigDecimal num, int tickSize) {
        return Value.newBuilder()
                .setListValue(ListValue.newBuilder()
                        .addValues(Value.newBuilder()
                                .setStringValue(price.setScale(tickSize, RoundingMode.HALF_EVEN).toString()).build())
                        .addValues(Value.newBuilder().setStringValue(num.toString()).build()).build())
                .build();
    }
}
