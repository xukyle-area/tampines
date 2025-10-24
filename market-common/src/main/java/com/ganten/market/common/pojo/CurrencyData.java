package com.ganten.market.common.pojo;

public class CurrencyData {
    private String baseCurrency;
    private String quoteCurrency;
    private String priceIncrement;
    private String sizeIncrement;
    private String type;
    private long timestamp;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public String getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(String priceIncrement) {
        this.priceIncrement = priceIncrement;
    }

    public String getSizeIncrement() {
        return sizeIncrement;
    }

    public void setSizeIncrement(String sizeIncrement) {
        this.sizeIncrement = sizeIncrement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CurrencyData r = new CurrencyData();

        public Builder baseCurrency(String v) {
            r.baseCurrency = v;
            return this;
        }

        public Builder quoteCurrency(String v) {
            r.quoteCurrency = v;
            return this;
        }

        public Builder priceIncrement(String v) {
            r.priceIncrement = v;
            return this;
        }

        public Builder sizeIncrement(String v) {
            r.sizeIncrement = v;
            return this;
        }

        public Builder type(String v) {
            r.type = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public CurrencyData build() {
            return r;
        }
    }
}
