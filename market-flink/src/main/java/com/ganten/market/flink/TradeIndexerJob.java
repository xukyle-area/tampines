package com.ganten.market.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.pojo.CandleData;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.aggregate.ResultEventAggregate;
import com.ganten.market.flink.sink.TradeBatchSink;
import com.ganten.market.flink.sink.TradeSingleSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import com.twitter.chill.protobuf.ProtobufSerializer;


public final class TradeIndexerJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(TradeIndexerJob.class.getClassLoader().getResourceAsStream(args[0]));

        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.ofParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment()
                .setParallelism(Integer.parseInt(parameterTool.get("process.parallelism")));
        FlinkUtils.configureStreamExecutionEnvironment(see, parameterTool);

        see.getConfig().registerTypeWithKryoSerializer(ResultEventHolder.class, ProtobufSerializer.class);

        see.getConfig().registerTypeWithKryoSerializer(CandleData.class, ProtobufSerializer.class);

        final DataStreamSource<ResultEventHolder> tradeStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source")
                        .setParallelism(Integer.parseInt(parameterTool.get("kafka.parallelism")));
        final KeyedStream<ResultEventHolder, Long> keyedStream = tradeStream.keyBy(ResultEventHolder::getContractId);

        final int tradeParallelism = parameterTool.getInt("trade.sink.parallelism");
        keyedStream.addSink(new TradeSingleSink("redis")).name("trade_redis_sink").uid("trade_redis_sink")
                .setParallelism(tradeParallelism);
        keyedStream.addSink(new TradeSingleSink("mqtt")).name("trade_mqtt_sink").uid("trade_mqtt_sink")
                .setParallelism(tradeParallelism);
        keyedStream
                .window(TumblingProcessingTimeWindows
                        .of(Time.seconds(parameterTool.getInt("trade.dynamo.sink.window", 1))))
                .aggregate(new ResultEventAggregate()).name("trade_dynamo_agg").uid("trade_dynamo_agg")
                .addSink(new TradeBatchSink()).name("trade_dynamo_sink").uid("trade_dynamo_sink");

        see.execute(parameterTool.get("job.name"));
    }

}
