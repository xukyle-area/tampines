package com.ganten.market.flink.utils;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.process.CandleCalculator;
import com.ganten.market.flink.sink.CandleDataSink;
import com.google.common.collect.Maps;

public final class FlinkUtils {

    public static void configureStreamExecutionEnvironment(StreamExecutionEnvironment see,
            ParameterTool parameterTool) {
        see.setParallelism(Integer.parseInt(parameterTool.get("process.parallelism", "1")));
        if (parameterTool.has("checkpoint.dir")) {
            final String checkpointDir = parameterTool.get("checkpoint.dir");
            final int checkpointInterval = Integer.parseInt(parameterTool.get("checkpoint.interval"));
            see.enableCheckpointing(checkpointInterval);
            see.getCheckpointConfig().enableExternalizedCheckpoints(
                    CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            see.getCheckpointConfig().setCheckpointStorage(checkpointDir);
            see.getCheckpointConfig().enableUnalignedCheckpoints();
            see.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        }
        see.getConfig().setGlobalJobParameters(parameterTool);
    }

    public static OffsetsInitializer ofConfig(String offsetConfig) {
        if ("earliest".equals(offsetConfig)) {
            return OffsetsInitializer.earliest();
        } else if ("latest".equals(offsetConfig)) {
            return OffsetsInitializer.latest();
        } else {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
        }
    }

    public static void calculateCandle(KeyedStream<ResultEventHolder, Long> keyedStream, final int resolution,
            String slotSharingGroup) {
        keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(resolution))).process(new CandleCalculator())
                .name("candle_calculator_" + resolution).uid("candle_calculator_" + resolution)
                .slotSharingGroup(slotSharingGroup).addSink(new CandleDataSink(resolution))
                .name("candle_sink_" + resolution).uid("candle_sink_" + resolution).slotSharingGroup(slotSharingGroup);
    }

    public static Map<String, String> toMap(Properties properties) {
        Map<String, String> result = Maps.newHashMap();
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String name = (String) propertyNames.nextElement();
            String value = properties.getProperty(name);
            result.put(name, value);
        }
        return result;
    }


    private FlinkUtils() {}

}
