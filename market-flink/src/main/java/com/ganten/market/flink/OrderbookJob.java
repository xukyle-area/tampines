package com.ganten.market.flink;


import static com.ganten.market.common.constants.Constants.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.flink.OrderBook;
import com.ganten.market.flink.process.BestQuoteProcess;
import com.ganten.market.flink.process.DeDuplicator;
import com.ganten.market.flink.sink.OrderBookSink;
import com.ganten.market.flink.sink.TickSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;

public final class OrderbookJob {

    private static final String ORDERBOOK_SINK = "orderbook_sink";

    public static void main(String[] args) throws Exception {

        InputConfig inputConfig = InputConfig.build("orderbook", "orderbook");

        KafkaSource<OrderBook> source = KafkaSourceUtils.of(inputConfig, OrderBook.class);
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(ONE);
        FlinkUtils.configureSee(see, inputConfig);
        see.setRestartStrategy(RestartStrategies.fixedDelayRestart(FIVE, FIVE_THOUSAND));

        SingleOutputStreamOperator<OrderBook> streamOperator = see
                .fromSource(source, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE).keyBy(OrderBook::getContractId)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(THOUSAND))).process(new DeDuplicator<>());

        // write orderbook to redis
        streamOperator.addSink(new OrderBookSink()).name(ORDERBOOK_SINK).uid(ORDERBOOK_SINK).setParallelism(ONE);

        // calculate best bid and ask to tick
        streamOperator.process(new BestQuoteProcess()).addSink(new TickSink()).name("best_quote_sink")
                .uid("best_quote_sink").setParallelism(ONE);

        see.execute(inputConfig.getJobName());
    }

}
