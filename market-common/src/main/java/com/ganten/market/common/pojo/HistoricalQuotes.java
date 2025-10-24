package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class HistoricalQuotes {
    private List<HistoricalQuote> historicalQuotes = new ArrayList<>();

    public List<HistoricalQuote> getHistoricalQuotes() {
        return historicalQuotes;
    }

    public void setHistoricalQuotes(List<HistoricalQuote> v) {
        this.historicalQuotes = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final HistoricalQuotes r = new HistoricalQuotes();

        public Builder historicalQuotes(java.util.List<HistoricalQuote> v) {
            r.historicalQuotes = v;
            return this;
        }

        public HistoricalQuotes build() {
            return r;
        }
    }
}
