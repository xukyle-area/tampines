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

    public static void main(String[] args) throws Exception {
        InputConfig inputConfig = InputConfig.build(JOB_TOPIC, JOB_NAME);

        KafkaSource<Order> source = KafkaSourceUtils.of(inputConfig, Order.class);
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkUtils.configureSee(see, inputConfig);

        DataStreamSource<Order> orderStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE).setParallelism(ONE);
        // key by contractId
        KeyedStream<Order, Long> keyedStream = orderStream.keyBy(Order::getContractId);

        // build a
        keyedStream.process(new OrderBookProcessor()).name(JOB_NAME).uid(JOB_NAME + "-process").setParallelism(ONE)
                .addSink(new OrderBookSink()).name(JOB_NAME).uid(JOB_NAME + "-sink").setParallelism(ONE);
        see.execute(JOB_NAME + "-job");
    }

}
