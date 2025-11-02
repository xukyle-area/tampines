package com.ganten.market.flink.job;

import static com.ganten.market.common.constants.Constants.KAFKA_SOURCE;
import static com.ganten.market.common.constants.Constants.ONE;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.flink.config.InputConfig;
import com.ganten.market.flink.process.CandleProcessor;
import com.ganten.market.flink.sink.CandleSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;

public final class CandleJob {

    private static final String JOB_NAME = "candle";
    private static final String JOB_TOPIC = "trade";

    private static final int[] RESOLUTIONS = {60, 300, 900, 3600, 21600, 86400};

    /**
     * @param args ["candle.properties"]
     */
    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = InputConfig.build(JOB_TOPIC, JOB_NAME);

        KafkaSource<Trade> source = KafkaSourceUtils.of(inputConfig, Trade.class);
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkUtils.configureSee(see, inputConfig);

        DataStreamSource<Trade> tradeStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE).setParallelism(ONE);
        // key by contractId
        KeyedStream<Trade, Long> keyedStream = tradeStream.keyBy(Trade::getContractId);

        for (int resolution : RESOLUTIONS) {
            CandleJob.calculate(keyedStream, resolution);
        }
        see.execute(JOB_NAME);
    }

    public static void calculate(KeyedStream<Trade, Long> keyedStream, final int resolution) {
        String calculatorName = "candle_calculator_" + resolution;
        String sinkName = "candle_sink_" + resolution;
        String slotSharingGroup = "candle_" + resolution;

        keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(resolution)))
                // calculate candle by resolution
                .process(new CandleProcessor()).name(calculatorName).uid(calculatorName)
                // set slot sharing group
                .slotSharingGroup(slotSharingGroup)
                // sink candle to redis
                .addSink(new CandleSink(resolution)).name(sinkName)
                // set slot sharing group
                .uid(sinkName).slotSharingGroup(slotSharingGroup);
    }
}
