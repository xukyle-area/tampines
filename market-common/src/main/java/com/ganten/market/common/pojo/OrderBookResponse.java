package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.Value;

public class OrderBookResponse {
    private List<Value> asks = new ArrayList<>();
    private List<Value> bids = new ArrayList<>();
    private long timestamp;
    private long updateId;

    public List<Value> getAsks() {
        return asks;
    }

    public void setAsks(List<Value> asks) {
        this.asks = asks;
    }

    public List<Value> getBids() {
        return bids;
    }

    public void setBids(List<Value> bids) {
        this.bids = bids;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }
}
