package com.ganten.market.outer.history.hashkey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.outer.history.BaseHistoryTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashkeyHistoryTask extends BaseHistoryTask {

    public List<DayHistoryQuote> getLatestDayCandle(Contract contract, int limit) {
        List<Kline> fetchKlines = null;
        try {
            String symbol = contract.getBase() + contract.getQuote();
            fetchKlines = HashkeyHistoryFetcher.fetchKlines(symbol, "1d", limit);
        } catch (IOException e) {
            log.error("Failed to fetch klines for contract: {}", contract, e);
        }
        if (fetchKlines == null || fetchKlines.isEmpty()) {
            return null;
        }

        List<DayHistoryQuote> result = new ArrayList<>();
        for (Kline kline : fetchKlines) {
            DayHistoryQuote quote = new DayHistoryQuote(kline.getOpenTime(), contract.getId(), Market.HASHKEY,
                    kline.getClosePrice().toPlainString());
            result.add(quote);
        }
        return result;
    }

    public List<DayHistoryQuote> getDayCandleBetween(Contract contract, long start, long end) {
        List<Kline> fetchKlines = null;
        try {
            String symbol = contract.getBase() + contract.getQuote();
            fetchKlines = HashkeyHistoryFetcher.fetchKlines(symbol, "1d", 100, start, end);
        } catch (IOException e) {
            log.error("Failed to fetch klines for contract: {}", contract, e);
        }
        if (fetchKlines == null || fetchKlines.isEmpty()) {
            return null;
        }
        List<DayHistoryQuote> result = new ArrayList<>();
        for (Kline kline : fetchKlines) {
            DayHistoryQuote quote = new DayHistoryQuote(kline.getOpenTime(), contract.getId(), Market.HASHKEY,
                    kline.getClosePrice().toPlainString());
            result.add(quote);
        }
        return result;
    }
}
