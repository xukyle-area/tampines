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
import com.ganten.market.flink.process.DeDuplicator;
import com.ganten.market.flink.sink.TickSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import com.twitter.chill.protobuf.ProtobufSerializer;

public final class TickIndexerJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(TickIndexerJob.class.getClassLoader().getResourceAsStream(args[0]));
        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.ofParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment()
                .setParallelism(Integer.parseInt(parameterTool.get("process.parallelism")));
        FlinkUtils.configureStreamExecutionEnvironment(see, parameterTool);

        see.getConfig().registerTypeWithKryoSerializer(ResultEventHolder.class, ProtobufSerializer.class);
        see.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 5 * 1000));

        final long windowSizeMillis = Long.parseLong(parameterTool.get("window.size.millis", "1000"));
        see.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(Integer.parseInt(parameterTool.get("kafka.parallelism")))
                .keyBy(ResultEventHolder::getContractId)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(windowSizeMillis)))
                .process(new DeDuplicator()).filter(t -> t.getResult_event_type() == ResultEventType.TICK)
                .addSink(new TickSink(Market.GANTEN)).name("tickSink").uid("tickSink");

        see.execute(parameterTool.get("job.name"));
    }

}
