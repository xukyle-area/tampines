package com.ganten.market.flink;

import java.util.List;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import com.ganten.market.common.model.TradeData;
import com.ganten.market.common.pojo.Last24HData;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.aggregate.Last24HAggregator;
import com.ganten.market.flink.process.Last24HCalculator;
import com.ganten.market.flink.sink.Last24HSink;
import com.ganten.market.flink.utils.FlinkUtils;
import com.ganten.market.flink.utils.KafkaSourceUtils;
import com.twitter.chill.protobuf.ProtobufSerializer;

public class Last24HJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool =
                ParameterTool.fromPropertiesFile(Last24HJob.class.getClassLoader().getResourceAsStream(args[0]));
        final KafkaSource<ResultEventHolder> source = KafkaSourceUtils.ofParameterTool(parameterTool);

        final StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        FlinkUtils.configureStreamExecutionEnvironment(see, parameterTool);

        see.getConfig().registerTypeWithKryoSerializer(ResultEventHolder.class, ProtobufSerializer.class);

        see.getConfig().registerTypeWithKryoSerializer(Last24HData.class, ProtobufSerializer.class);
        see.getCheckpointConfig().enableUnalignedCheckpoints();


        final SingleOutputStreamOperator<TradeData> dataStream =
                see.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source").map(t -> {
                    TradeData res = new TradeData();
                    res.setContractId(t.getContractId());
                    res.setId(t.getTrade().getId());
                    res.setPrice(t.getTrade().getPrice().toPlainString());
                    res.setVolume(t.getTrade().getVolume().toPlainString());
                    res.setTimestamp(t.getTimestamp());
                    return res;
                });

        final KeyedStream<TradeData, Long> keyedStream = dataStream.keyBy(TradeData::getContractId);

        keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(1))).aggregate(new Last24HAggregator())
                .keyBy((KeySelector<List<TradeData>, Long>) value -> value.get(0).getContractId())
                .process(new Last24HCalculator()).setParallelism(parameterTool.getInt("process.parallelism"))
                .addSink(new Last24HSink()).name("Last24HSink").uid("Last24HSink")
                .setParallelism(parameterTool.getInt("process.parallelism"));

        see.execute(parameterTool.get("job.name"));
    }
}
