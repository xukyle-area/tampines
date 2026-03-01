package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.common.flink.output.Candle;

/**
 * @param <IN>  The type of the input value: {@link Trade}
 * @param <OUT> The type of the output value: {@link Candle}
 * @param <KEY> The type of the key: {@link Long}
 * @param <W>   The type of {@code Window} that this window function can be applied on: {@link TimeWindow}
 */
public class CandleProcessor extends ProcessWindowFunction<Trade, Candle, Long, TimeWindow> {
    private static final long serialVersionUID = -3539822931681340622L;

    private static final Logger logger = LoggerFactory.getLogger(CandleProcessor.class);

    // 使用 Flink 状态存储已处理的 trade ID，跨窗口去重
    private transient ValueState<Set<Long>> processedTradeIdsState;

    @Override
    public void open(Configuration config) {
        // 配置状态 TTL，1小时后自动清理，避免状态无限增长
        StateTtlConfig ttlConfig =
                StateTtlConfig.newBuilder(Time.hours(1)).setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired).build();

        ValueStateDescriptor<Set<Long>> descriptor =
                new ValueStateDescriptor<>("processed-trade-ids", TypeInformation.of(new TypeHint<Set<Long>>() {}));
        descriptor.enableTimeToLive(ttlConfig);
        processedTradeIdsState = getRuntimeContext().getState(descriptor);
    }

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

        // 从状态中获取已处理的 trade ID 集合（跨窗口去重）
        Set<Long> processedIds = processedTradeIdsState.value();
        if (processedIds == null) {
            processedIds = new HashSet<>();
        }

        for (Trade tradeInfo : elements) {
            long tradeId = tradeInfo.getId();
            // 使用状态进行跨窗口去重
            if (processedIds.contains(tradeId)) {
                logger.debug("Skipping duplicate trade: {}", tradeId);
                continue;
            }
            processedIds.add(tradeId);

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

        // 更新状态
        processedTradeIdsState.update(processedIds);

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
