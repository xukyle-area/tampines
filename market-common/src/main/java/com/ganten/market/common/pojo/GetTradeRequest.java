package com.ganten.market.common.pojo;

public class GetTradeRequest {
    private String symbol;
    private long startTime;
    private long endTime;
    private long id;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GetTradeRequest r = new GetTradeRequest();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder startTime(long v) {
            r.startTime = v;
            return this;
        }

        public Builder endTime(long v) {
            r.endTime = v;
            return this;
        }

        public Builder id(long v) {
            r.id = v;
            return this;
        }

        public GetTradeRequest build() {
            return r;
        }
    }
}
