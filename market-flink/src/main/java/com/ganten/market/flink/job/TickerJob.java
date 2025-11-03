package com.ganten.market.flink.job;

import static com.ganten.market.common.constants.Constants.KAFKA_SOURCE;
import static com.ganten.market.common.constants.Constants.ONE;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.flink.input.Trade;
import com.ganten.market.flink.config.InputConfig;
import com.ganten.market.flink.process.TickerAggregator;
import com.ganten.market.flink.process.TickerProcessor;
import com.ganten.market.flink.sink.TickerSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickerJob {
    private static final String JOB_NAME = "ticker";
    private static final String JOB_TOPIC = "trade";

    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = InputConfig.build(JOB_TOPIC, JOB_NAME);

        KafkaSource<Trade> source = KafkaSourceUtils.of(inputConfig, Trade.class);
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkUtils.configureSee(see, inputConfig);
        DataStreamSource<Trade> tradeStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE).setParallelism(ONE);
        // key by contractId
        KeyedStream<Trade, Long> keyedStream = tradeStream.keyBy(Trade::getContractId);

        String processorName = JOB_NAME + "-processor";
        String sinkName = JOB_NAME + "-sink";
        keyedStream
                // windowing
                .window(SlidingEventTimeWindows.of(Time.hours(24), Time.seconds(1)))
                // configure aggregate & process function
                .aggregate(new TickerAggregator(), new TickerProcessor())
                // configure processor name and uid
                .name(processorName).uid(processorName).setParallelism(ONE)
                // configure sink
                .addSink(new TickerSink())
                // configure sink name and uid
                .name(sinkName).uid(sinkName).setParallelism(ONE);
    }
}
