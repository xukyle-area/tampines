package com.ganten.market.flink;

import static com.ganten.market.common.constants.Constants.ONE;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.flink.Trade;
import com.ganten.market.flink.sink.TradeSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;

public final class TradeJob {
    private static final String TRADE_REDIS_SINK = "trade_redis_sink";

    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = new InputConfig();

        // 交易信息流
        final KafkaSource<Trade> source = KafkaSourceUtils.of(inputConfig, Trade.class);

        // flink stream execution environment, mainly for checkpoint configuration
        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(ONE);
        FlinkUtils.configureSee(see, inputConfig);
        final DataStreamSource<Trade> tradeStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), Constants.KAFKA_SOURCE).setParallelism(ONE);

        // keyed stream by contract id
        final KeyedStream<Trade, Long> keyedStream = tradeStream.keyBy(Trade::getContractId);

        // add sink
        keyedStream.addSink(new TradeSink()).name(TRADE_REDIS_SINK).uid(TRADE_REDIS_SINK).setParallelism(ONE);
        see.execute(inputConfig.getJobName());
    }
}
