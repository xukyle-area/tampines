package com.ganten.market.flink.job;


import static com.ganten.market.common.constants.Constants.KAFKA_SOURCE;
import static com.ganten.market.common.constants.Constants.ONE;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.ganten.market.common.flink.input.Order;
import com.ganten.market.flink.config.InputConfig;
import com.ganten.market.flink.process.OrderBookProcessor;
import com.ganten.market.flink.sink.OrderBookSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;

public final class OrderbookJob {

    private static final String JOB_NAME = "orderbook";
    private static final String JOB_TOPIC = "order";
    private static final int[] RESOLUTIONS = {1, 5, 10, 100};

    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = InputConfig.build(JOB_TOPIC, JOB_NAME);

        KafkaSource<Order> source = KafkaSourceUtils.of(inputConfig, Order.class);
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkUtils.configureSee(see, inputConfig);

        DataStreamSource<Order> orderStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE).setParallelism(ONE);
        // key by contractId
        KeyedStream<Order, Long> keyedStream = orderStream.keyBy(Order::getContractId);

        for (int resolution : RESOLUTIONS) {
            OrderbookJob.calculate(keyedStream, resolution);
        }
        see.execute(JOB_NAME + "-job");
    }

    private static void calculate(KeyedStream<Order, Long> keyedStream, final int resolution) {
        keyedStream
                // add process orderbook
                .process(new OrderBookProcessor(resolution))
                // set job name and uid
                .name(JOB_NAME).uid(JOB_NAME + "-process-" + resolution).setParallelism(ONE)
                // sink orderbook to redis
                .addSink(new OrderBookSink())
                // set job name and uid
                .name(JOB_NAME).uid(JOB_NAME + "-sink-" + resolution).setParallelism(ONE);
    }
}
