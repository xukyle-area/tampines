package com.ganten.market.common.pojo;

public class RateRequest {
    private String currencies;
    private String quoteCurrency;

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
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
        private final RateRequest r = new RateRequest();

        public Builder currencies(String v) {
            r.currencies = v;
            return this;
        }

        public Builder quoteCurrency(String v) {
            r.quoteCurrency = v;
            return this;
        }

        public RateRequest build() {
            return r;
        }
    }
}
