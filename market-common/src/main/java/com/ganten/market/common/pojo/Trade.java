package com.ganten.market.common.pojo;

public class Trade {
    private long id;
    private String price;
    private String volume;
    private boolean isBidderMaker;
    private String time;
    private String side;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public boolean isBidderMaker() {
        return isBidderMaker;
    }

    public void setBidderMaker(boolean bidderMaker) {
        isBidderMaker = bidderMaker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Trade r = new Trade();

        public Builder id(long v) {
            r.id = v;
            return this;
        }

        public Builder price(String v) {
            r.price = v;
            return this;
        }

        public Builder volume(String v) {
            r.volume = v;
            return this;
        }

        public Builder isBidderMaker(boolean v) {
            r.isBidderMaker = v;
            return this;
        }

        public Builder time(String v) {
            r.time = v;
            return this;
        }

        public Builder side(String v) {
            r.side = v;
            return this;
        }

        public Trade build() {
            return r;
        }
    }
}
