package com.ganten.market.flink.process;

import java.math.BigDecimal;
import org.apache.flink.api.common.functions.AggregateFunction;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.mediate.TickerAccumulator;

/**
 * ProcessWindowFunction to compute Tick data from TickAccumulator.
 *
 * @param <IN>  The type of the input value: {@link Trade}
 * @param <ACC> – The type of the accumulator: {@link TickerAccumulator}
 * @param <OUT> The type of the output value: {@link TickerAccumulator}
 */
public class TickerAggregator implements AggregateFunction<Trade, TickerAccumulator, TickerAccumulator> {

    @Override
    public TickerAccumulator createAccumulator() {
        return new TickerAccumulator();
    }

    @Override
    public TickerAccumulator add(Trade trade, TickerAccumulator acc) {
        BigDecimal price = trade.getPrice();
        BigDecimal vol = trade.getVolume();
        long tradeTime = trade.getTime();

        // 使用时间戳判断首笔和末笔交易
        if (tradeTime < acc.getFirstTimestamp()) {
            acc.setFirstTimestamp(tradeTime);
            acc.setFirstPrice(price);
        }
        if (tradeTime > acc.getLastTimestamp()) {
            acc.setLastTimestamp(tradeTime);
            acc.setLastPrice(price);
        }

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
    public TickerAccumulator getResult(TickerAccumulator acc) {
        return acc;
    }

    @Override
    public TickerAccumulator merge(TickerAccumulator a, TickerAccumulator b) {
        TickerAccumulator merged = new TickerAccumulator();

        // 根据时间戳判断哪个累加器包含更早/更晚的交易
        if (a.getFirstTimestamp() <= b.getFirstTimestamp()) {
            merged.setFirstPrice(a.getFirstPrice());
            merged.setFirstTimestamp(a.getFirstTimestamp());
        } else {
            merged.setFirstPrice(b.getFirstPrice());
            merged.setFirstTimestamp(b.getFirstTimestamp());
        }

        if (a.getLastTimestamp() >= b.getLastTimestamp()) {
            merged.setLastPrice(a.getLastPrice());
            merged.setLastTimestamp(a.getLastTimestamp());
        } else {
            merged.setLastPrice(b.getLastPrice());
            merged.setLastTimestamp(b.getLastTimestamp());
        }

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
