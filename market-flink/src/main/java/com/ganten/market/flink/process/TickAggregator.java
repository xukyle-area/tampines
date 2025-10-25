package com.ganten.market.flink.process;

import java.math.BigDecimal;
import org.apache.flink.api.common.functions.AggregateFunction;
import com.ganten.market.common.flink.TickAccumulator;
import com.ganten.market.common.flink.Trade;

/**
 * ProcessWindowFunction to compute Tick data from TickAccumulator.
 *
 * @param <IN>  The type of the input value: {@link Trade}
 * @param <ACC> – The type of the accumulator: {@link TickAccumulator}
 * @param <OUT> The type of the output value: {@link TickAccumulator}
 */
public class TickAggregator implements AggregateFunction<Trade, TickAccumulator, TickAccumulator> {

    @Override
    public TickAccumulator createAccumulator() {
        return new TickAccumulator();
    }

    @Override
    public TickAccumulator add(Trade trade, TickAccumulator acc) {
        BigDecimal price = trade.getPrice();
        BigDecimal vol = trade.getVolume();

        if (acc.getFirstPrice() == null) {
            acc.setFirstPrice(price);
        }
        acc.setLastPrice(price);
        acc.setVolume(acc.getVolume().add(vol));

        if (acc.getHighest() == null || price.compareTo(acc.getHighest()) > 0) {
            acc.setHighest(price);
        }
        if (acc.getLowest() == null || price.compareTo(acc.getLowest()) < 0) {
            acc.setLowest(price);
        }

        return acc;
    }

    @Override
    public TickAccumulator getResult(TickAccumulator acc) {
        return acc;
    }

    @Override
    public TickAccumulator merge(TickAccumulator a, TickAccumulator b) {
        TickAccumulator merged = new TickAccumulator();
        merged.setFirstPrice(a.getFirstPrice() != null ? a.getFirstPrice() : b.getFirstPrice());
        merged.setLastPrice(b.getLastPrice() != null ? b.getLastPrice() : a.getLastPrice());
        merged.setVolume(a.getVolume().add(b.getVolume()));
        merged.setHighest(max(a.getHighest(), b.getHighest()));
        merged.setLowest(min(a.getLowest(), b.getLowest()));
        return merged;
    }

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return a.min(b);
    }

    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return a.max(b);
    }
}
