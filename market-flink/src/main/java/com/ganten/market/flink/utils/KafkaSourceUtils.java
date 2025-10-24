package com.ganten.market.flink.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import com.ganten.market.common.pojo.ResultEventHolder;
import com.ganten.market.flink.serialization.ResultEventHolderDeserializer;

public final class KafkaSourceUtils {

    public static KafkaSource<ResultEventHolder> ofParameterTool(ParameterTool parameterTool)
            throws NoSuchMethodException {

        final String bootStrapServers = parameterTool.get("kafka.bootstrap.servers");
        final String topic = parameterTool.get("kafka.topic");
        final String groupId = parameterTool.get("kafka.group.id");
        final KafkaSourceBuilder<ResultEventHolder> sourceBuilder =
                KafkaSource.<ResultEventHolder>builder().setBootstrapServers(bootStrapServers).setTopics(topic)
                        .setGroupId(groupId).setProperty("partition.discovery.interval.ms", "10000")
                        .setValueOnlyDeserializer(new ResultEventHolderDeserializer());
        sourceBuilder.setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST));
        return sourceBuilder.build();
    }

    private KafkaSourceUtils() {}
}
