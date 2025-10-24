package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class QuoteBatch {
    private List<Quote> quotes = new ArrayList<>();

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuoteBatch r = new QuoteBatch();

        public Builder quotes(java.util.List<Quote> v) {
            r.quotes = v;
            return this;
        }

        public QuoteBatch build() {
            return r;
        }
    }
}
