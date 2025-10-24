package com.ganten.market.common.pojo;

public class Last24HData {
    private String change;
    private String changePercent;
    private String max;
    private String min;
    private String vol;

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Last24HData d = new Last24HData();

        public Builder change(String v) {
            d.change = v;
            return this;
        }

        public Builder changePercent(String v) {
            d.changePercent = v;
            return this;
        }

        public Builder max(String v) {
            d.max = v;
            return this;
        }

        public Builder min(String v) {
            d.min = v;
            return this;
        }

        public Builder vol(String v) {
            d.vol = v;
            return this;
        }

        public Last24HData build() {
            return d;
        }
    }
}
