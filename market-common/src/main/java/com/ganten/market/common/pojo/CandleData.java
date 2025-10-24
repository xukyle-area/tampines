package com.ganten.market.common.pojo;

public class CandleData {
    private String startTime;
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CandleData c = new CandleData();

        public Builder startTime(String v) {
            c.startTime = v;
            return this;
        }

        public Builder open(String v) {
            c.open = v;
            return this;
        }

        public Builder close(String v) {
            c.close = v;
            return this;
        }

        public Builder high(String v) {
            c.high = v;
            return this;
        }

        public Builder low(String v) {
            c.low = v;
            return this;
        }

        public Builder volume(String v) {
            c.volume = v;
            return this;
        }

        public CandleData build() {
            return c;
        }
    }

    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        sb.append(startTime).append(",").append(open).append(",").append(close).append(",").append(high).append(",")
                .append(low).append(",").append(volume);
        return sb.toString().getBytes();
    }
}
