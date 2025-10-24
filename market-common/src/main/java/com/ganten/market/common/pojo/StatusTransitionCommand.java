package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class StatusTransitionCommand {
    private long timestamp;
    private String status; // common.MatchingEngineStatus stored as String
    private BigDecimal lastTradedPrice;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLastTradedPrice() {
        return lastTradedPrice;
    }

    public void setLastTradedPrice(BigDecimal lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final StatusTransitionCommand r = new StatusTransitionCommand();

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public Builder status(String v) {
            r.status = v;
            return this;
        }

        public Builder lastTradedPrice(BigDecimal v) {
            r.lastTradedPrice = v;
            return this;
        }

        public StatusTransitionCommand build() {
            return r;
        }
    }
}
