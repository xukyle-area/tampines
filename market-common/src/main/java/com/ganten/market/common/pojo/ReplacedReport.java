package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class ReplacedReport {
    private String execId; // deprecated
    private BigDecimal totalOrderQty;
    private BigDecimal totalOrderQuoteQty;
    private BigDecimal price;
    private BigDecimal stopPrice; // optional

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public BigDecimal getTotalOrderQty() {
        return totalOrderQty;
    }

    public void setTotalOrderQty(BigDecimal totalOrderQty) {
        this.totalOrderQty = totalOrderQty;
    }

    public BigDecimal getTotalOrderQuoteQty() {
        return totalOrderQuoteQty;
    }

    public void setTotalOrderQuoteQty(BigDecimal totalOrderQuoteQty) {
        this.totalOrderQuoteQty = totalOrderQuoteQty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ReplacedReport r = new ReplacedReport();

        public Builder execId(String v) {
            r.execId = v;
            return this;
        }

        public Builder totalOrderQty(BigDecimal v) {
            r.totalOrderQty = v;
            return this;
        }

        public Builder totalOrderQuoteQty(BigDecimal v) {
            r.totalOrderQuoteQty = v;
            return this;
        }

        public Builder price(BigDecimal v) {
            r.price = v;
            return this;
        }

        public Builder stopPrice(BigDecimal v) {
            r.stopPrice = v;
            return this;
        }

        public ReplacedReport build() {
            return r;
        }
    }
}
