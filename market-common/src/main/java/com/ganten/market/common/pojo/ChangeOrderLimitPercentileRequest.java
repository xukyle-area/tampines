package com.ganten.market.common.pojo;

public class ChangeOrderLimitPercentileRequest {
    private long timestamp;
    private int orderLimitPercentile;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getOrderLimitPercentile() {
        return orderLimitPercentile;
    }

    public void setOrderLimitPercentile(int orderLimitPercentile) {
        this.orderLimitPercentile = orderLimitPercentile;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ChangeOrderLimitPercentileRequest r = new ChangeOrderLimitPercentileRequest();

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public Builder orderLimitPercentile(int v) {
            r.orderLimitPercentile = v;
            return this;
        }

        public ChangeOrderLimitPercentileRequest build() {
            return r;
        }
    }
}
