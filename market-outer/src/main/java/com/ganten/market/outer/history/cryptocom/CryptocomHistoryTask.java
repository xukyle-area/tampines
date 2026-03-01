package com.ganten.market.outer.history.cryptocom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.enums.Market;
import com.ganten.market.common.model.DayHistoryQuote;
import com.ganten.market.outer.history.BaseHistoryTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptocomHistoryTask extends BaseHistoryTask {

    @Override
    public List<DayHistoryQuote> getLatestDayCandle(Contract contract, int limit) {
        List<CryptocomKline> fetchKlines = null;
        try {
            String instrumentName = contract.getBase() + "_" + contract.getQuote();
            fetchKlines = CryptocomHistoryFetcher.fetchKlines(instrumentName, "1D", limit);
        } catch (IOException e) {
            log.error("Failed to fetch klines for contract: {}", contract, e);
        }
        if (fetchKlines == null || fetchKlines.isEmpty()) {
            return null;
        }

        List<DayHistoryQuote> result = new ArrayList<>();
        for (CryptocomKline kline : fetchKlines) {
            DayHistoryQuote quote = new DayHistoryQuote(kline.getOpenTime(), contract.getId(), Market.CRYPTO_COM,
                    kline.getClosePrice().toPlainString());
            result.add(quote);
        }
        return result;
    }

    @Override
    public List<DayHistoryQuote> getDayCandleBetween(Contract contract, long start, long end) {
        List<CryptocomKline> fetchKlines = null;
        try {
            String instrumentName = contract.getBase() + "_" + contract.getQuote();
            fetchKlines = CryptocomHistoryFetcher.fetchKlines(instrumentName, "1D", 300, start, end);
        } catch (IOException e) {
            log.error("Failed to fetch klines for contract: {}", contract, e);
        }
        if (fetchKlines == null || fetchKlines.isEmpty()) {
            return null;
        }

        List<DayHistoryQuote> result = new ArrayList<>();
        for (CryptocomKline kline : fetchKlines) {
            DayHistoryQuote quote = new DayHistoryQuote(kline.getOpenTime(), contract.getId(), Market.CRYPTO_COM,
                    kline.getClosePrice().toPlainString());
            result.add(quote);
        }
        return result;
    }
}
