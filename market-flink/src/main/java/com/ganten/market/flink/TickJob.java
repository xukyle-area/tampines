package com.ganten.market.flink;

import static com.ganten.market.common.constants.Constants.ONE;
import java.time.Duration;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.flink.Trade;
import com.ganten.market.flink.process.TickAggregator;
import com.ganten.market.flink.process.TickWindowFunction;
import com.ganten.market.flink.sink.TickSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickJob {

    public static final String TICK_REDIS_SINK = "tick_redis_sink";

    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = InputConfig.build("trade", "tick");
        log.info("TickJob started with config: {}", inputConfig);
        final KafkaSource<Trade> source = KafkaSourceUtils.of(inputConfig, Trade.class);

        // flink stream execution environment, mainly for checkpoint configuration
        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(ONE);
        FlinkUtils.configureSee(see, inputConfig);
        WatermarkStrategy<Trade> ws = WatermarkStrategy.<Trade>forBoundedOutOfOrderness(Duration.ofSeconds(ONE))
                .withTimestampAssigner((t, ts) -> t.getTime());
        DataStream<Trade> trades = see.fromSource(source, ws, Constants.KAFKA_SOURCE).setParallelism(ONE);

        // --- 24h 滑动窗口，每分钟滑动一次 ---
        trades.keyBy(Trade::getContractId).window(SlidingEventTimeWindows.of(Time.hours(24), Time.seconds(30)))
                .aggregate(new TickAggregator(), new TickWindowFunction()).addSink(new TickSink()).name(TICK_REDIS_SINK)
                .uid(TICK_REDIS_SINK).setParallelism(ONE);

        see.execute(inputConfig.getJobName());
    }
}
