package com.ganten.market.common.pojo;

public class OrderBookRequest {
    private int depth;
    private String symbol;
    private String grouping;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderBookRequest r = new OrderBookRequest();

        public Builder depth(int v) {
            r.depth = v;
            return this;
        }

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder grouping(String v) {
            r.grouping = v;
            return this;
        }

        public OrderBookRequest build() {
            return r;
        }
    }
}
