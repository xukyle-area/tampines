package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class Tick {
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal last;

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal b) {
        bid = b;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal a) {
        ask = a;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal l) {
        last = l;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Tick r = new Tick();

        public Builder bid(BigDecimal v) {
            r.bid = v;
            return this;
        }

        public Builder ask(BigDecimal v) {
            r.ask = v;
            return this;
        }

        public Builder last(BigDecimal v) {
            r.last = v;
            return this;
        }

        public Tick build() {
            return r;
        }
    }
}
