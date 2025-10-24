package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class OrderBook {
    private List<OrderBookTuple> bids = new ArrayList<>();
    private List<OrderBookTuple> asks = new ArrayList<>();
    private long updateId;
    private long firstId;
    private long lastId;

    public List<OrderBookTuple> getBids() {
        return bids;
    }

    public void setBids(List<OrderBookTuple> bids) {
        this.bids = bids;
    }

    public List<OrderBookTuple> getAsks() {
        return asks;
    }

    public void setAsks(List<OrderBookTuple> asks) {
        this.asks = asks;
    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long v) {
        updateId = v;
    }

    public long getFirstId() {
        return firstId;
    }

    public void setFirstId(long v) {
        firstId = v;
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long v) {
        lastId = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderBook r = new OrderBook();

        public Builder bids(java.util.List<OrderBookTuple> v) {
            r.bids = v;
            return this;
        }

        public Builder asks(java.util.List<OrderBookTuple> v) {
            r.asks = v;
            return this;
        }

        public Builder updateId(long v) {
            r.updateId = v;
            return this;
        }

        public Builder firstId(long v) {
            r.firstId = v;
            return this;
        }

        public Builder lastId(long v) {
            r.lastId = v;
            return this;
        }

        public OrderBook build() {
            return r;
        }
    }
}
