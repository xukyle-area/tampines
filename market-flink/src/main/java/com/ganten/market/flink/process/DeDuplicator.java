package com.ganten.market.flink.process;

import java.util.HashSet;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import com.ganten.market.common.flink.BaseObject;

public class DeDuplicator<T extends BaseObject> extends ProcessWindowFunction<T, T, Long, TimeWindow> {

    private static final long serialVersionUID = 7438254167809849816L;

    @Override
    public void process(Long contractId, Context context, Iterable<T> elements, Collector<T> out) throws Exception {
        HashSet<T> hashSet = new HashSet<>();

        for (T element : elements) {
            hashSet.add(element);
        }
        for (T element : hashSet) {
            out.collect(element);
        }
    }
}
