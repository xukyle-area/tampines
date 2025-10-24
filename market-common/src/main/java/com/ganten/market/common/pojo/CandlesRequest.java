package com.ganten.market.common.pojo;

public class CandlesRequest {
    private String symbol;
    private int resolution;
    private long startTime;
    private long endTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CandlesRequest r = new CandlesRequest();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder resolution(int v) {
            r.resolution = v;
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

        public CandlesRequest build() {
            return r;
        }
    }
}
