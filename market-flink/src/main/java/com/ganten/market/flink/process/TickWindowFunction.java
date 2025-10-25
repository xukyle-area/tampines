package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.flink.Tick;
import com.ganten.market.common.flink.TickAccumulator;


/**
 * ProcessWindowFunction to compute Tick data from TickAccumulator.
 *
 * @param <IN>  The type of the input value: {@link TickAccumulator}
 * @param <OUT> The type of the output value: {@link Tick}
 * @param <KEY> The type of the key: {@link Contract}
 */
public class TickWindowFunction extends ProcessWindowFunction<TickAccumulator, Tick, Long, TimeWindow> {
    @Override
    public void process(Long contractId, Context ctx, Iterable<TickAccumulator> elements, Collector<Tick> out) {
        TickAccumulator acc = elements.iterator().next();
        if (acc.getFirstPrice() == null || acc.getLastPrice() == null) {
            return;
        }

        BigDecimal change = acc.getLastPrice().subtract(acc.getFirstPrice());
        BigDecimal percent = BigDecimal.ZERO;
        if (acc.getFirstPrice().compareTo(BigDecimal.ZERO) != 0) {
            percent = change.divide(acc.getFirstPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        Tick tick = new Tick();
        tick.setContractId(contractId);
        tick.setHighest(acc.getHighest());
        tick.setLowest(acc.getLowest());
        tick.setVolume(acc.getVolume());
        tick.setChange(change);
        tick.setChangePercent(percent.setScale(2, RoundingMode.HALF_UP) + "%");

        out.collect(tick);
    }
}
