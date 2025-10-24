package com.ganten.market.common.pojo;

public class HistoryIndexPriceRequest {
    private String symbol;
    private String startTime;
    private String endTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final HistoryIndexPriceRequest r = new HistoryIndexPriceRequest();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder startTime(String v) {
            r.startTime = v;
            return this;
        }

        public Builder endTime(String v) {
            r.endTime = v;
            return this;
        }

        public HistoryIndexPriceRequest build() {
            return r;
        }
    }
}
