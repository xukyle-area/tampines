package com.ganten.market.flink;

import java.time.Duration;
import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.ganten.market.common.pojo.CandleData;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import com.twitter.chill.protobuf.ProtobufSerializer;

public final class CandleIndexerJob {

    private static final String CANDLE_SLOT_SHARING_GROUP = "candle_slot_sharing_group";

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(CandleIndexerJob.class.getClassLoader().getResourceAsStream(args[0]));
        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.ofParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment()
                .setParallelism(Integer.parseInt(parameterTool.get("process.parallelism")));
        FlinkUtils.configureStreamExecutionEnvironment(see, parameterTool);

        see.getConfig().registerTypeWithKryoSerializer(ResultEventHolder.class, ProtobufSerializer.class);

        see.getConfig().registerTypeWithKryoSerializer(CandleData.class, ProtobufSerializer.class);

        final WatermarkStrategy<ResultEventHolder> watermarkStrategy = WatermarkStrategy
                .<ResultEventHolder>forBoundedOutOfOrderness(Duration.ofSeconds(20))
                .withIdleness(Duration.ofSeconds(10)).withTimestampAssigner(
                        (TimestampAssignerSupplier<ResultEventHolder>) context -> (TimestampAssigner<ResultEventHolder>) (
                                element, recordTimestamp) -> element.getTimestamp());

        final DataStreamSource<ResultEventHolder> tradeStream =
                see.fromSource(source, watermarkStrategy, "kafka source")
                        .setParallelism(Integer.parseInt(parameterTool.get("kafka.parallelism")));
        final KeyedStream<ResultEventHolder, Long> keyedStream = tradeStream.keyBy(ResultEventHolder::getContractId);

        FlinkUtils.calculateCandle(keyedStream, 60, CANDLE_SLOT_SHARING_GROUP);
        FlinkUtils.calculateCandle(keyedStream, 300, CANDLE_SLOT_SHARING_GROUP);
        FlinkUtils.calculateCandle(keyedStream, 900, CANDLE_SLOT_SHARING_GROUP);
        FlinkUtils.calculateCandle(keyedStream, 3600, CANDLE_SLOT_SHARING_GROUP);
        FlinkUtils.calculateCandle(keyedStream, 21600, CANDLE_SLOT_SHARING_GROUP);
        FlinkUtils.calculateCandle(keyedStream, 86400, CANDLE_SLOT_SHARING_GROUP);

        see.execute(parameterTool.get("job.name"));
    }

}
