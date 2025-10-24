package com.ganten.market.common.pojo;

public class HistoricalQuoteRequest {
    // MARKET enum mapped to String here
    private String market;
    private long contractId;
    private String symbol;
    private int resolution;
    private long startTime;
    private long endTime;

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
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
        private final HistoricalQuoteRequest r = new HistoricalQuoteRequest();

        public Builder market(String v) {
            r.market = v;
            return this;
        }

        public Builder contractId(long v) {
            r.contractId = v;
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

        public Builder startTime(long v) {
            r.startTime = v;
            return this;
        }

        public Builder endTime(long v) {
            r.endTime = v;
            return this;
        }

        public HistoricalQuoteRequest build() {
            return r;
        }
    }
}
