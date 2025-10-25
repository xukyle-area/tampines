package com.ganten.market.flink.utils;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import com.ganten.market.common.constants.Constants;
import com.google.common.collect.Maps;

public final class FlinkUtils {

    public static void configureSEE(StreamExecutionEnvironment see, ParameterTool parameterTool) {
        see.setParallelism(Constants.ONE);
        if (parameterTool.has("checkpoint.dir")) {
            final String checkpointDir = parameterTool.get("checkpoint.dir");
            final int checkpointInterval = Integer.parseInt(parameterTool.get("checkpoint.interval"));
            see.enableCheckpointing(checkpointInterval);
            see.getCheckpointConfig().setExternalizedCheckpointCleanup(
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
