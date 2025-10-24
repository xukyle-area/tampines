package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class RateResponse {
    public static class RateData {
        private String currency;
        private String rate;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }
    }

    private List<RateData> data = new ArrayList<>();
    private long timestamp;
    private String quoteCurrency;

    public List<RateData> getData() {
        return data;
    }

    public void setData(List<RateData> data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RateResponse r = new RateResponse();

        public Builder data(java.util.List<RateData> v) {
            r.data = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public Builder quoteCurrency(String v) {
            r.quoteCurrency = v;
            return this;
        }

        public RateResponse build() {
            return r;
        }
    }
}
