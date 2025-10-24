package com.ganten.market.outter.writer;

import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.common.model.RealTimeQuote;

public interface QuoteWriter {

    default void updateRealTimeQuote(RealTimeQuote realTimeQuote) {};

    default void updateDayHistoryQuote(DayHistoryQuote dayHistoryQuote) {};
}
