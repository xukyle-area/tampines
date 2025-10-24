package com.ganten.market.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.common.pojo.ResultEventType;
import com.ganten.market.flink.aggregate.ResultEventAggregate;
import com.ganten.market.flink.process.DiffOrderbookCalculator;
import com.ganten.market.flink.sink.DiffOrderBookSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import com.twitter.chill.protobuf.ProtobufSerializer;

public class DiffOrderbookIndexerJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool = ParameterTool
                .fromPropertiesFile(DiffOrderbookIndexerJob.class.getClassLoader().getResourceAsStream(args[0]));
        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.ofParameterTool(parameterTool);
        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        FlinkUtils.configureStreamExecutionEnvironment(see, parameterTool);
        see.getConfig().registerTypeWithKryoSerializer(ResultEventHolder.class, ProtobufSerializer.class);
        see.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 5 * 1000));

        final long windowSizeMillis = Long.parseLong(parameterTool.get("window.size.millis", "1000"));
        see.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .filter(t -> t.getResult_event_type() == ResultEventType.RESULT_EVENT_TYPE_DIFFORDERBOOK
                        || t.getResult_event_type() == ResultEventType.RESULT_EVENT_TYPE_DIFFORDERBOOKALL)
                .keyBy(ResultEventHolder::getContractId)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(windowSizeMillis)))
                .aggregate(new ResultEventAggregate()).process(new DiffOrderbookCalculator(parameterTool.toMap()))
                .addSink(new DiffOrderBookSink(Market.EXODUS)).name("diffOrderBookSink").uid("diffOrderBookSink")
                .setParallelism(parameterTool.getInt("process.parallelism"));

        see.execute(parameterTool.get("job.name"));
    }

}
