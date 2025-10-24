package com.ganten.market.flink.sink;

import java.util.Arrays;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.flink.operator.AllOperators;
import com.ganten.market.flink.operator.MqttWriter;
import com.ganten.market.flink.operator.RedisQuoteOperator;

public class AbstractSink<T> extends RichSinkFunction<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected AllOperators writers;

    @Override
    public void open(Configuration parameters) {
        Map<String, String> mapConf = getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap();
        writers = new AllOperators(Arrays.asList(new RedisQuoteOperator(mapConf), new MqttWriter(mapConf)));
    }
}
