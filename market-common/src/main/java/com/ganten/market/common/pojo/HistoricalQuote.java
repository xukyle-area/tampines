package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class HistoricalQuote {
    private long startTime;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final HistoricalQuote r = new HistoricalQuote();

        public Builder startTime(long v) {
            r.startTime = v;
            return this;
        }

        public Builder open(BigDecimal v) {
            r.open = v;
            return this;
        }

        public Builder close(BigDecimal v) {
            r.close = v;
            return this;
        }

        public Builder high(BigDecimal v) {
            r.high = v;
            return this;
        }

        public Builder low(BigDecimal v) {
            r.low = v;
            return this;
        }

        public Builder volume(BigDecimal v) {
            r.volume = v;
            return this;
        }

        public HistoricalQuote build() {
            return r;
        }
    }
}
