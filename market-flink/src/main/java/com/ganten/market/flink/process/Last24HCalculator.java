package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.model.TradeData;
import com.ganten.market.common.pojo.Last24HData;
import lombok.Data;

public class Last24HCalculator extends KeyedProcessFunction<Long, List<TradeData>, Tuple2<Long, Last24HData>> {

    private static final Logger logger = LoggerFactory.getLogger(Last24HCalculator.class);

    private transient MapState<Long, TradeData> mapState;

    @Data
    static class CalculatorResult {
        BigDecimal totalVolume;
        BigDecimal maxPrice;
        BigDecimal minPrice;

        CalculatorResult() {
            totalVolume = BigDecimal.ZERO;
            maxPrice = BigDecimal.valueOf(Double.MIN_VALUE);
            minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        }
    }

    private void calculate(CalculatorResult calculatorResult, TradeData trade) {
        final BigDecimal volume = new BigDecimal(trade.getVolume());
        final BigDecimal price = new BigDecimal(trade.getPrice());
        calculatorResult.totalVolume = calculatorResult.totalVolume.add(volume);
        if (price.compareTo(calculatorResult.maxPrice) > 0) {
            calculatorResult.maxPrice = price;
        }
        if (price.compareTo(calculatorResult.minPrice) < 0) {
            calculatorResult.minPrice = price;
        }
    }

    @Override
    public void open(Configuration conf) throws Exception {
        final int timeToLiveHours = 25;
        final StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.hours(timeToLiveHours))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired).build();
        final MapStateDescriptor<Long, TradeData> descriptor =
                new MapStateDescriptor<>("tradeDataMap", Long.class, TradeData.class);
        descriptor.enableTimeToLive(ttlConfig);
        mapState = getRuntimeContext().getMapState(descriptor);
    }

    @Override
    public void processElement(List<TradeData> elements, Context ctx, Collector<Tuple2<Long, Last24HData>> out)
            throws Exception {
        // register the timer in case there is no trades for a long time
        ctx.timerService().registerProcessingTimeTimer(getNextMinuteMillis());

        // check the trade data in latest 24 hours
        boolean updated = false;
        final CalculatorResult calculatorResult = new CalculatorResult();

        for (TradeData trade : elements) {
            if (trade.isWithin24Hours()) {
                updated = true;
                mapState.put(trade.getId(), trade);
            }
        }

        final Iterator<Entry<Long, TradeData>> iterator = mapState.entries().iterator();

        int count = 0;

        while (iterator.hasNext()) {
            final Entry<Long, TradeData> entry = iterator.next();
            final TradeData trade = entry.getValue();
            if (!trade.isWithin24Hours()) {
                iterator.remove();
                continue;
            }
            calculate(calculatorResult, trade);
            count++;
        }
        logger.info("there is {} entries in map", count);
        if (updated) {

            Last24HData last24hData = new Last24HData();
            last24hData.setMax(calculatorResult.maxPrice.toString());
            last24hData.setMin(calculatorResult.minPrice.toString());
            last24hData.setVol(calculatorResult.totalVolume.toString());

            final Tuple2<Long, Last24HData> result = new Tuple2<>(ctx.getCurrentKey(), last24hData);
            logger.info("result is {}", result);
            out.collect(result);
        }
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<Long, Last24HData>> out) throws Exception {
        logger.info("timer invoked at {}", timestamp);
        ctx.timerService().registerProcessingTimeTimer(getNextMinuteMillis());
        boolean updated = false;
        final CalculatorResult calculatorResult = new CalculatorResult();
        final Iterator<Entry<Long, TradeData>> iterator = mapState.entries().iterator();
        while (iterator.hasNext()) {
            final Entry<Long, TradeData> entry = iterator.next();
            final TradeData trade = entry.getValue();
            if (!trade.isWithin24Hours()) {
                iterator.remove();
                continue;
            }
            updated = true;
            calculate(calculatorResult, trade);
        }
        if (updated) {
            Last24HData last24hData = new Last24HData();
            last24hData.setMax(calculatorResult.maxPrice.toString());
            last24hData.setMin(calculatorResult.minPrice.toString());
            last24hData.setVol(calculatorResult.totalVolume.toString());
            final Tuple2<Long, Last24HData> result = new Tuple2<>(ctx.getCurrentKey(), last24hData);
            logger.info("result is {}", result);
            out.collect(result);
        }

    }

    private long getNextMinuteMillis() {
        final long now = System.currentTimeMillis();
        return now / 60000L * 60000L + 60000L;
    }


}
