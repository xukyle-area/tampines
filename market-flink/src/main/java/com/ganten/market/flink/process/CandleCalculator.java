package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.flink.Candle;
import com.ganten.market.common.flink.Trade;

/**
 * @param <IN>  The type of the input value: {@link Trade}
 * @param <OUT> The type of the output value: {@link Candle}
 * @param <KEY> The type of the key: {@link Long}
 * @param <W>   The type of {@code Window} that this window function can be applied on: {@link TimeWindow}
 */
public class CandleCalculator extends ProcessWindowFunction<Trade, Candle, Long, TimeWindow> {
    private static final long serialVersionUID = -3539822931681340622L;

    private static final Logger logger = LoggerFactory.getLogger(CandleCalculator.class);

    @Override
    public void open(Configuration config) {}

    /**
     * Calculate candle data from trade events in the window
     * resulting candle data will be collected to downstream sink
     * - long: contractId
     * - CandleData: candle data
     *
     * @param key      contractId
     * @param context  window context
     * @param elements trade events
     * @param out      collector
     */
    @Override
    public void process(Long key, Context context, Iterable<Trade> elements, Collector<Candle> out) throws Exception {
        logger.info("window start: {}, window end {}", context.window().getStart(), context.window().getEnd());
        BigDecimal open = BigDecimal.ZERO;
        BigDecimal close = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ZERO;
        BigDecimal low = new BigDecimal(Double.MAX_VALUE);
        BigDecimal volume = BigDecimal.ZERO;
        boolean opened = false;
        Set<Long> trades = new HashSet<>();
        for (Trade tradeInfo : elements) {
            long tradeId = tradeInfo.getId();
            if (!trades.add(tradeId)) {
                continue;
            }
            final String priceValue = tradeInfo.getPrice().toString();
            final String volumeValue = tradeInfo.getVolume().toString();
            if (!StringUtils.isEmpty(priceValue) && !StringUtils.isEmpty(volumeValue)) {
                final BigDecimal price = new BigDecimal(priceValue);
                final BigDecimal curVolume = new BigDecimal(volumeValue);
                if (!opened) {
                    open = price;
                    opened = true;
                }
                close = price;
                if (price.compareTo(high) > 0) {
                    high = price;
                }
                if (price.compareTo(low) < 0) {
                    low = price;
                }
                volume = volume.add(curVolume);
            }
        }

        Candle candleData = new Candle();
        candleData.setStartTime(Long.toString(context.window().getStart()));
        candleData.setOpen(open.toString());
        candleData.setClose(close.toString());
        candleData.setHigh(high.toString());
        candleData.setLow(low.toString());
        candleData.setVolume(volume.toString());

        logger.info("collection candle data {}", candleData);
        out.collect(candleData);
    }
}
