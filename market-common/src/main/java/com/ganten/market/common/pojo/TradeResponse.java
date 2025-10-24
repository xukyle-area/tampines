package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class TradeResponse {
    private List<Trade> data = new ArrayList<>();
    private long timestamp;

    public List<Trade> getData() {
        return data;
    }

    public void setData(List<Trade> data) {
        this.data = data;
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
        private final TradeResponse r = new TradeResponse();

        public Builder data(java.util.List<Trade> v) {
            r.data = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public TradeResponse build() {
            return r;
        }
    }
}
