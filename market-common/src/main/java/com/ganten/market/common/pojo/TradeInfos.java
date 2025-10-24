package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class TradeInfos {
    private List<TradeInfo> trades = new ArrayList<>();

    public List<TradeInfo> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeInfo> v) {
        this.trades = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TradeInfos r = new TradeInfos();

        public Builder trades(java.util.List<TradeInfo> v) {
            r.trades = v;
            return this;
        }

        public TradeInfos build() {
            return r;
        }
    }
}
