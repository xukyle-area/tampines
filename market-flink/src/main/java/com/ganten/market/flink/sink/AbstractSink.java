package com.ganten.market.flink.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.flink.writer.CompositeWriter;

public class AbstractSink<T> extends RichSinkFunction<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected CompositeWriter compositeWriter;

    @Override
    public void open(Configuration parameters) {
        compositeWriter = new CompositeWriter();
    }
}
