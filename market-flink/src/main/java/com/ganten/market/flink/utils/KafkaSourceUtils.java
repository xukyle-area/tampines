package com.ganten.market.flink.utils;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.flink.config.InputConfig;
import com.ganten.market.flink.serialization.Deserializer;

public final class KafkaSourceUtils {

    public static <T> KafkaSource<T> of(InputConfig config, Class<T> clazz) throws NoSuchMethodException {

        final String topic = config.getTopic();
        final String groupId = config.getGroupId();

        final KafkaSourceBuilder<T> sourceBuilder =
                KafkaSource.<T>builder().setBootstrapServers(Constants.BOOTSTRAP_SERVERS).setTopics(topic)
                        .setGroupId(groupId).setProperty("partition.discovery.interval.ms", "10000")
                        .setValueOnlyDeserializer(new Deserializer<>(clazz));

        sourceBuilder.setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST));
        return sourceBuilder.build();
    }

    private KafkaSourceUtils() {}
}
