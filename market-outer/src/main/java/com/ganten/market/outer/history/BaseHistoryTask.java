package com.ganten.market.outer.history;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.outer.writer.MysqlQuoteWriter;
import com.ganten.market.outer.writer.QuoteWriter;

public abstract class BaseHistoryTask {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public abstract List<DayHistoryQuote> getLatestDayCandle(Contract symbol, int limit);

    public abstract List<DayHistoryQuote> getDayCandleBetween(Contract symbol, long start, long end);

    private QuoteWriter mysqlQuoteWriter = new MysqlQuoteWriter();

    public void run() {
        for (Contract symbol : Contract.values()) {
            List<DayHistoryQuote> latestDayCandles = this.getLatestDayCandle(symbol, 1);
            if (latestDayCandles == null || latestDayCandles.isEmpty()) {
                continue;
            }
            mysqlQuoteWriter.updateHistoryQuote(latestDayCandles.get(0));
        }
    }

    /**
     * 恢复
     * @param symbol 符号
     * @param start yyyyMMdd
     * @param end yyyyMMdd
     */
    public void recover(Contract symbol, int start, int end) {
        long startTimestamp = this.convertToTimestamp(start);
        long endTimestamp = this.convertToTimestamp(end);

        List<DayHistoryQuote> dayCandles = this.getDayCandleBetween(symbol, startTimestamp, endTimestamp);
        if (dayCandles == null || dayCandles.isEmpty()) {
            return;
        }
        for (DayHistoryQuote candle : dayCandles) {
            mysqlQuoteWriter.updateHistoryQuote(candle);
        }
    }

    /**
     * 将 yyyyMMdd 格式的日期转换为时间戳（毫秒）
     */
    private long convertToTimestamp(int date) {
        String dateStr = String.valueOf(date);
        LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER);
        return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
}
