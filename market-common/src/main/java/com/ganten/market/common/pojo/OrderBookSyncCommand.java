package com.ganten.market.common.pojo;

public class OrderBookSyncCommand {
    private long timestamp;

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
        private final OrderBookSyncCommand r = new OrderBookSyncCommand();

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public OrderBookSyncCommand build() {
            return r;
        }
    }
}
