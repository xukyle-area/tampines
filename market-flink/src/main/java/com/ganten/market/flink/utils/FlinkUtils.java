package com.ganten.market.flink.utils;

import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.flink.InputConfig;

public final class FlinkUtils {

    public static void configureSee(StreamExecutionEnvironment see, InputConfig config) {
        see.setParallelism(Constants.ONE);
        if (config.getCheckpointDir() != null && !config.getCheckpointDir().isEmpty()) {
            final String checkpointDir = config.getCheckpointDir();
            final int checkpointInterval = Integer.parseInt(config.getCheckpointInterval());
            see.enableCheckpointing(checkpointInterval);
            see.getCheckpointConfig().setExternalizedCheckpointCleanup(
                    CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            see.getCheckpointConfig().setCheckpointStorage(checkpointDir);
            see.getCheckpointConfig().enableUnalignedCheckpoints();
            see.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        }
    }
}
