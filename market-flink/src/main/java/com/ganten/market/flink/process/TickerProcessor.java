package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.flink.mediate.TickerAccumulator;
import com.ganten.market.common.flink.output.Ticker;


/**
 * ProcessWindowFunction to compute Tick data from TickAccumulator.
 *
 * @param <IN>  The type of the input value: {@link TickerAccumulator}
 * @param <OUT> The type of the output value: {@link Ticker}
 * @param <KEY> The type of the key: {@link Contract}
 */
public class TickerProcessor extends ProcessWindowFunction<TickerAccumulator, Ticker, Long, TimeWindow> {
    @Override
    public void process(Long contractId, Context ctx, Iterable<TickerAccumulator> elements, Collector<Ticker> out) {
        TickerAccumulator acc = elements.iterator().next();
        if (acc.getFirstPrice() == null || acc.getLastPrice() == null) {
            return;
        }

        BigDecimal change = acc.getLastPrice().subtract(acc.getFirstPrice());
        BigDecimal percent = BigDecimal.ZERO;
        if (acc.getFirstPrice().compareTo(BigDecimal.ZERO) != 0) {
            percent = change.divide(acc.getFirstPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        Ticker ticker = new Ticker();
        ticker.setContractId(contractId);
        ticker.setHighest(acc.getHighest());
        ticker.setLowest(acc.getLowest());
        ticker.setVolume(acc.getVolume());
        ticker.setChange(change);
        ticker.setLast(acc.getLastPrice());
        ticker.setChangePercent(percent.setScale(2, RoundingMode.HALF_UP));
        out.collect(ticker);
    }
}
