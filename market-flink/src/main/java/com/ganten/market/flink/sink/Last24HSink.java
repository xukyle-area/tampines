package com.ganten.market.flink.sink;

import org.apache.flink.api.java.tuple.Tuple2;
import com.ganten.market.common.pojo.Last24HData;
import com.ganten.market.common.pojo.Market;

public class Last24HSink extends AbstractSink<Tuple2<Long, Last24HData>> {

    @Override
    public void invoke(Tuple2<Long, Last24HData> value, Context context) {
        writers.update24HQuote(value.f1, Market.GANTEN, value.f0);
    }
}
