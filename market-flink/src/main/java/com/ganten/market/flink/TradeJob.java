package com.ganten.market.flink;

import static com.ganten.market.common.constants.Constants.ONE;
import static com.ganten.market.common.constants.Constants.TRADE_DYNAMO_AGG;
import static com.ganten.market.common.constants.Constants.TRADE_DYNAMO_SINK;
import static com.ganten.market.common.constants.Constants.TRADE_REDIS_SINK;
import java.util.HashMap;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.aggregate.ResultEventAggregate;
import com.ganten.market.flink.sink.TradeBatchSink;
import com.ganten.market.flink.sink.TradeSingleSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;


public final class TradeJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(TradeJob.class.getClassLoader().getResourceAsStream(args[0]));

        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.fromParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(ONE);
        FlinkUtils.configureSEE(see, parameterTool);

        final DataStreamSource<ResultEventHolder> tradeStream = see.fromSource(
            source,
            WatermarkStrategy.noWatermarks(),
            Constants.KAFKA_SOURCE
        ).setParallelism(ONE);

        final KeyedStream<ResultEventHolder, Long> keyedStream = tradeStream.keyBy(ResultEventHolder::getContractId);

        keyedStream.addSink(new TradeSingleSink(new HashMap<>()))
            .name(TRADE_REDIS_SINK)
            .uid(TRADE_REDIS_SINK)
            .setParallelism(ONE);

        keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(ONE)))
            .aggregate(new ResultEventAggregate())
            .name(TRADE_DYNAMO_AGG)
            .uid(TRADE_DYNAMO_AGG)
            .addSink(new TradeBatchSink())
            .name(TRADE_DYNAMO_SINK)
            .uid(TRADE_DYNAMO_SINK);

        see.execute("trade_job");
    }
}
