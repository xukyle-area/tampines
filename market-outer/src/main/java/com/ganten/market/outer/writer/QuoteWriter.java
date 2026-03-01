package com.ganten.market.outer.writer;

import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.common.model.RealTimeQuote;

public abstract class QuoteWriter {
    public abstract void updateRealTimeQuote(RealTimeQuote realTimeQuote);

    public abstract void updateHistoryQuote(DayHistoryQuote dayHistoryQuote);
}
