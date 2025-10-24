package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class IndexPrice {
    private BigDecimal indexPrice;

    public BigDecimal getIndexPrice() {
        return indexPrice;
    }

    public void setIndexPrice(BigDecimal v) {
        this.indexPrice = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final IndexPrice r = new IndexPrice();

        public Builder indexPrice(BigDecimal v) {
            r.indexPrice = v;
            return this;
        }

        public IndexPrice build() {
            return r;
        }
    }
}
