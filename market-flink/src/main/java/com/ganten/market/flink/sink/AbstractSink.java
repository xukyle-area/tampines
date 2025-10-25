package com.ganten.market.flink.sink;

import java.util.Arrays;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.flink.writer.CompositeWriter;
import com.ganten.market.flink.writer.MqttWriter;
import com.ganten.market.flink.writer.RedisWriter;

public class AbstractSink<T> extends RichSinkFunction<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected CompositeWriter compositeWriter;

    @Override
    public void open(Configuration parameters) {
        Map<String, String> mapConf = getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap();
        compositeWriter = new CompositeWriter(Arrays.asList(new RedisWriter(mapConf), new MqttWriter(mapConf)));
    }
}
