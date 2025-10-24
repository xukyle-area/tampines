package com.ganten.market.flink.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ganten.market.common.pojo.ResultEventHolder;

public class DeDuplicator extends ProcessWindowFunction<ResultEventHolder, ResultEventHolder, Long, TimeWindow> {

    private static final Logger logger = LoggerFactory.getLogger(DeDuplicator.class);

    private static final long serialVersionUID = 7438254167809849816L;

    @Override
    public void process(Long aLong, Context context, Iterable<ResultEventHolder> elements,
            Collector<ResultEventHolder> out) throws Exception {
        final Map<Long, ResultEventHolder> deDupMap = new HashMap<>();
        for (ResultEventHolder element : elements) {
            final long contractId = element.getContractId();
            logger.debug("iterating {}", element);
            deDupMap.put(contractId, element);
        }
        for (Entry<Long, ResultEventHolder> entry : deDupMap.entrySet()) {
            logger.debug("collect result {}", entry.getValue());
            out.collect(entry.getValue());
        }
    }
}
