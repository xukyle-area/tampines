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
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.process.CandleCalculator;
import com.ganten.market.flink.sink.CandleDataSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;

public final class CandleJob {

    private static final String CANDLE_SLOT_SHARING_GROUP = "candle_slot_sharing_group";

    /**
     * @param args ["candle.properties"]
     */
    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(CandleJob.class.getClassLoader().getResourceAsStream(args[0]));
        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.fromParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkUtils.configureSEE(see, parameterTool);

        final WatermarkStrategy<ResultEventHolder> watermarkStrategy = WatermarkStrategy
                .<ResultEventHolder>forBoundedOutOfOrderness(Duration.ofSeconds(20))
                .withIdleness(Duration.ofSeconds(10)).withTimestampAssigner(
                        (TimestampAssignerSupplier<ResultEventHolder>) context -> (TimestampAssigner<ResultEventHolder>) (
                                element, recordTimestamp) -> element.getTimestamp());

        final DataStreamSource<ResultEventHolder> tradeStream =
                see.fromSource(source, watermarkStrategy, Constants.KAFKA_SOURCE).setParallelism(1);
        final KeyedStream<ResultEventHolder, Long> keyedStream = tradeStream.keyBy(ResultEventHolder::getContractId);

        CandleJob.calculateCandle(keyedStream, 60);
        CandleJob.calculateCandle(keyedStream, 300);
        CandleJob.calculateCandle(keyedStream, 900);
        CandleJob.calculateCandle(keyedStream, 3600);
        CandleJob.calculateCandle(keyedStream, 21600);
        CandleJob.calculateCandle(keyedStream, 86400);

        see.execute(Constants.CANDLE_JOB);
    }

    public static void calculateCandle(KeyedStream<ResultEventHolder, Long> keyedStream, final int resolution) {
        keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(resolution))).process(new CandleCalculator())
                .name("candle_calculator_" + resolution).uid("candle_calculator_" + resolution)
                .slotSharingGroup(CANDLE_SLOT_SHARING_GROUP).addSink(new CandleDataSink(resolution))
                .name("candle_sink_" + resolution).uid("candle_sink_" + resolution)
                .slotSharingGroup(CANDLE_SLOT_SHARING_GROUP);
    }
}
