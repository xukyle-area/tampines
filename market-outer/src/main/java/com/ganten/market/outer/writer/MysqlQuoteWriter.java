package com.ganten.market.outer.writer;

import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.common.model.RealTimeQuote;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlQuoteWriter extends QuoteWriter {
    public void updateRealTimeQuote(RealTimeQuote realTimeQuote) {
        log.info("write to mysql: {}", realTimeQuote.toString());
    }

    public void updateHistoryQuote(DayHistoryQuote dayHistoryQuote) {
        log.info("write to mysql: {}", dayHistoryQuote.toString());
    }
}
