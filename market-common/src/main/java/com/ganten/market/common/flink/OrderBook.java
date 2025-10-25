package com.ganten.market.common.flink;

import static java.util.stream.Collectors.toList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.ganten.market.common.model.PriceQuantity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderBook extends BaseObject {
    private long updateId;
    private long firstId;
    private long lastId;
    private List<PriceQuantity> bids = new ArrayList<>();
    private List<PriceQuantity> asks = new ArrayList<>();


    private static final int MAX_SCALE = 14;

    public static OrderBook build(List<PriceQuantity> askList, List<PriceQuantity> bidList, String tickSize) {
        OrderBook orderBook = new OrderBook();
        orderBook.setAsks(new ArrayList<>());
        orderBook.setBids(new ArrayList<>());
        orderBook.setTimestamp(System.currentTimeMillis());

        BigDecimal grouping;
        askList.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        bidList.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        try {
            grouping = new BigDecimal(tickSize);
        } catch (Exception e) {
            List<PriceQuantity> asks = askList.stream()
                    .map(t -> build(t.getPrice().toString(), t.getQuantity().toString())).collect(toList());
            List<PriceQuantity> bids = bidList.stream()
                    .map(t -> build(t.getPrice().toString(), t.getQuantity().toString())).collect(toList());
            orderBook.getAsks().addAll(asks);
            orderBook.getBids().addAll(bids);
            return orderBook;
        }

        BigDecimal end = grouping;
        BigDecimal sum = BigDecimal.ZERO;
        int scale = grouping.setScale(MAX_SCALE, RoundingMode.HALF_EVEN).stripTrailingZeros().scale();
        for (PriceQuantity vs : askList) {
            BigDecimal price = vs.getPrice();
            BigDecimal num = vs.getQuantity();
            if (price.compareTo(end) <= 0) {
                sum = sum.add(num);
                continue;
            }
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                orderBook.getAsks().add(buildOrderBookValue(end, sum, scale));
            }
            end = grouping.multiply(price.divide(grouping, 0, RoundingMode.UP));
            sum = num;
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            orderBook.getAsks().add(buildOrderBookValue(end, sum, scale));
            sum = BigDecimal.ZERO;
        }

        end = BigDecimal.valueOf(Double.MAX_VALUE);
        for (PriceQuantity vs : bidList) {
            BigDecimal price = vs.getPrice();
            BigDecimal num = vs.getQuantity();
            if (price.compareTo(end) >= 0) {
                sum = sum.add(num);
                continue;
            }
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                orderBook.getBids().add(buildOrderBookValue(end, sum, scale));
            }
            end = grouping.multiply(price.divide(grouping, 0, RoundingMode.DOWN));
            sum = num;
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            orderBook.getBids().add(buildOrderBookValue(end, sum, scale));
        }
        return orderBook;
    }

    private static PriceQuantity build(String price, String num) {
        PriceQuantity priceQuantity = new PriceQuantity();
        priceQuantity.setPrice(new BigDecimal(price));
        priceQuantity.setQuantity(new BigDecimal(num));
        return priceQuantity;
    }

    private static PriceQuantity buildOrderBookValue(BigDecimal price, BigDecimal num, int tickSize) {
        PriceQuantity priceQuantity = new PriceQuantity();
        priceQuantity.setPrice(price.setScale(tickSize, RoundingMode.HALF_EVEN));
        priceQuantity.setQuantity(num);
        return priceQuantity;
    }
}
