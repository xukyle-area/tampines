package com.ganten.market.flink.sink;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.CandleData;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.common.pojo.TradeInfo;
import com.ganten.market.flink.utils.DecimalUtils;
import com.ganten.market.flink.writer.CompositeWriter;

public class TradeSingleSink extends RichSinkFunction<ResultEventHolder> {
    private static final Logger log = LoggerFactory.getLogger(TradeSingleSink.class);

    private CompositeWriter compositeWriter;

    private Map<Long, Long> contractCandleTimeMap = new HashMap<>();

    private static final int[] resolutions = {60, 300, 900, 3600, 21600, 86400};

    public TradeSingleSink(Map<String, String> parameterTool) {
        compositeWriter = new CompositeWriter(parameterTool);
    }

    @Override
    public void open(Configuration parameters) {
        Map<String, String> mapConf = getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap();
        compositeWriter = new CompositeWriter(mapConf);
    }

    @Override
    public void invoke(ResultEventHolder value, Context context) {
        compositeWriter.updateTrade(value.getTrade(), value.getContractId());

        long startingTimestamp = System.currentTimeMillis();
        for (int resolution : resolutions) {
            long startTime = value.getTimestamp() / (resolution * 1000L) * (resolution * 1000L);
            List<TradeInfo> trades =
                    quoteOperator.getTrade(value.getContractId(), startTime, startTime + resolution * 1000L);
            if (trades == null || trades.isEmpty()) {
                continue;
            }
            CandleData candleData = calculateCandleFromTrades(trades, startTime);
            compositeWriter.getCandleWriter().updateCandle(candleData, value.getContractId(), resolution);
        }
        long tempCandleTime = System.currentTimeMillis();
        contractCandleTimeMap.put(value.getContractId(), tempCandleTime);
        log.info("calculate temp candle cost ms : {}", tempCandleTime - startingTimestamp);
    }

    @Nullable
    private static CandleData calculateCandleFromTrades(@Nonnull List<TradeInfo> trades, long timeStamp) {
        if (trades.isEmpty()) {
            return null;
        }
        TradeInfo first = trades.get(0);
        BigDecimal open = first.getPrice();
        BigDecimal high = first.getPrice();
        BigDecimal low = first.getPrice();
        BigDecimal close = first.getPrice();
        BigDecimal volume = first.getVolume();

        CandleData candleData = new CandleData();
        for (int i = 1; i < trades.size(); i++) {
            final TradeInfo trade = trades.get(i);
            BigDecimal price = trade.getPrice();
            if (price.compareTo(high) > 0) {
                high = price;
            }
            if (price.compareTo(low) < 0) {
                low = price;
            }
            close = price;
            volume = volume.add(trade.getVolume());
        }
        candleData.setClose(DecimalUtils.decimalToString(close));
        candleData.setOpen(DecimalUtils.decimalToString(open));
        candleData.setHigh(DecimalUtils.decimalToString(high));
        candleData.setLow(DecimalUtils.decimalToString(low));
        candleData.setVolume(DecimalUtils.decimalToString(volume));
        candleData.setStartTime(String.valueOf(timeStamp));
        return candleData;
    }
}
