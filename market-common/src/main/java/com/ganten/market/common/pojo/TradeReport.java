package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class TradeReport {
    private String execId; // deprecated
    private BigDecimal lastFilledQty;
    private BigDecimal lastFilledPrice;
    private BigDecimal cumFilledOrderQty;
    private BigDecimal cumFilledOrderQuoteQty;
    private long tradeMatchId;
    private String orderStatus; // preserved as String to match common.OrderStatus enum textual
    private boolean isMaker;

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public BigDecimal getLastFilledQty() {
        return lastFilledQty;
    }

    public void setLastFilledQty(BigDecimal lastFilledQty) {
        this.lastFilledQty = lastFilledQty;
    }

    public BigDecimal getLastFilledPrice() {
        return lastFilledPrice;
    }

    public void setLastFilledPrice(BigDecimal lastFilledPrice) {
        this.lastFilledPrice = lastFilledPrice;
    }

    public BigDecimal getCumFilledOrderQty() {
        return cumFilledOrderQty;
    }

    public void setCumFilledOrderQty(BigDecimal cumFilledOrderQty) {
        this.cumFilledOrderQty = cumFilledOrderQty;
    }

    public BigDecimal getCumFilledOrderQuoteQty() {
        return cumFilledOrderQuoteQty;
    }

    public void setCumFilledOrderQuoteQty(BigDecimal cumFilledOrderQuoteQty) {
        this.cumFilledOrderQuoteQty = cumFilledOrderQuoteQty;
    }

    public long getTradeMatchId() {
        return tradeMatchId;
    }

    public void setTradeMatchId(long tradeMatchId) {
        this.tradeMatchId = tradeMatchId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isMaker() {
        return isMaker;
    }

    public void setMaker(boolean maker) {
        isMaker = maker;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TradeReport r = new TradeReport();

        public Builder execId(String v) {
            r.execId = v;
            return this;
        }

        public Builder lastFilledQty(BigDecimal v) {
            r.lastFilledQty = v;
            return this;
        }

        public Builder lastFilledPrice(BigDecimal v) {
            r.lastFilledPrice = v;
            return this;
        }

        public Builder cumFilledOrderQty(BigDecimal v) {
            r.cumFilledOrderQty = v;
            return this;
        }

        public Builder cumFilledOrderQuoteQty(BigDecimal v) {
            r.cumFilledOrderQuoteQty = v;
            return this;
        }

        public Builder tradeMatchId(long v) {
            r.tradeMatchId = v;
            return this;
        }

        public Builder orderStatus(String v) {
            r.orderStatus = v;
            return this;
        }

        public Builder isMaker(boolean v) {
            r.isMaker = v;
            return this;
        }

        public TradeReport build() {
            return r;
        }
    }
}
