package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class TradeInfo {
    private long id;
    private BigDecimal price;
    private BigDecimal volume;
    private boolean isBuyerMaker;
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal p) {
        this.price = p;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal v) {
        this.volume = v;
    }

    public boolean isBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(boolean b) {
        isBuyerMaker = b;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TradeInfo r = new TradeInfo();

        public Builder id(long v) {
            r.id = v;
            return this;
        }

        public Builder price(java.math.BigDecimal v) {
            r.price = v;
            return this;
        }

        public Builder volume(java.math.BigDecimal v) {
            r.volume = v;
            return this;
        }

        public Builder isBuyerMaker(boolean v) {
            r.isBuyerMaker = v;
            return this;
        }

        public Builder time(long v) {
            r.time = v;
            return this;
        }

        public TradeInfo build() {
            return r;
        }
    }

    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(price).append(",").append(volume).append(",").append(isBuyerMaker).append(",")
                .append(time);
        return sb.toString().getBytes();
    }

    public static TradeInfo parseFrom(byte[] data) {
        String s = new String(data);
        String[] parts = s.split(",");
        TradeInfo r = new TradeInfo();
        r.id = Long.parseLong(parts[0]);
        r.price = new BigDecimal(parts[1]);
        r.volume = new BigDecimal(parts[2]);
        r.isBuyerMaker = Boolean.parseBoolean(parts[3]);
        r.time = Long.parseLong(parts[4]);
        return r;
    }
}
