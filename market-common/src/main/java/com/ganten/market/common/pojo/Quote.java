package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class Quote {
    private String symbol;
    private BigDecimal last;
    private BigDecimal ask;
    private BigDecimal bid;
    private BigDecimal volume24h;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Quote r = new Quote();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder last(BigDecimal v) {
            r.last = v;
            return this;
        }

        public Builder ask(BigDecimal v) {
            r.ask = v;
            return this;
        }

        public Builder bid(BigDecimal v) {
            r.bid = v;
            return this;
        }

        public Builder volume24h(BigDecimal v) {
            r.volume24h = v;
            return this;
        }

        public Quote build() {
            return r;
        }
    }
}
