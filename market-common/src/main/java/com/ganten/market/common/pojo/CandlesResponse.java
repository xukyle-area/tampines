package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class CandlesResponse {
    private List<CandleData> data = new ArrayList<>();
    private String symbol;
    private int resolution;
    private long timestamp;

    public List<CandleData> getData() {
        return data;
    }

    public void setData(List<CandleData> data) {
        this.data = data;
    }

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
        private final CandlesResponse r = new CandlesResponse();

        public Builder data(List<CandleData> v) {
            r.data = v;
            return this;
        }

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder resolution(int v) {
            r.resolution = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public CandlesResponse build() {
            return r;
        }
    }
}
