package com.ganten.market.flink.process;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.CandleData;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.common.pojo.TradeInfo;

public class CandleCalculator
        extends ProcessWindowFunction<ResultEventHolder, Tuple2<Long, CandleData>, Long, TimeWindow> {

    private static final Logger logger = LoggerFactory.getLogger(CandleCalculator.class);
    private static final long serialVersionUID = -3539822931681340622L;

    @Override
    public void open(Configuration config) {}

    @Override
    public void process(Long key, Context context, Iterable<ResultEventHolder> elements,
            Collector<Tuple2<Long, CandleData>> out) throws Exception {
        logger.info("window start: {}, window end {}", context.window().getStart(), context.window().getEnd());
        BigDecimal open = BigDecimal.ZERO;
        BigDecimal close = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ZERO;
        BigDecimal low = new BigDecimal(Double.MAX_VALUE);
        BigDecimal volume = BigDecimal.ZERO;
        boolean opened = false;
        Set<Long> trades = new HashSet<>();
        for (ResultEventHolder resultEventHolder : elements) {
            final TradeInfo tradeInfo = resultEventHolder.getTrade();
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

        CandleData candleData = new CandleData();
        candleData.setStartTime(Long.toString(context.window().getStart()));
        candleData.setOpen(open.toString());
        candleData.setClose(close.toString());
        candleData.setHigh(high.toString());
        candleData.setLow(low.toString());
        candleData.setVolume(volume.toString());

        logger.info("collection candle data {}", candleData);
        out.collect(Tuple2.of(key, candleData));
    }

}
