package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class TickerResponse {
    public static class TickData {
        private String ask;
        private String bid;
        private String last;
        private String volume;
        private String highest24hours;
        private String lowest24hours;
        private String change24hours;
        private String changePercent24hours;
        private String symbol;
        private long timestamp;
        private String contractStatus;

        public String getAsk() {
            return ask;
        }

        public void setAsk(String ask) {
            this.ask = ask;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getHighest24hours() {
            return highest24hours;
        }

        public void setHighest24hours(String highest24hours) {
            this.highest24hours = highest24hours;
        }

        public String getLowest24hours() {
            return lowest24hours;
        }

        public void setLowest24hours(String lowest24hours) {
            this.lowest24hours = lowest24hours;
        }

        public String getChange24hours() {
            return change24hours;
        }

        public void setChange24hours(String change24hours) {
            this.change24hours = change24hours;
        }

        public String getChangePercent24hours() {
            return changePercent24hours;
        }

        public void setChangePercent24hours(String changePercent24hours) {
            this.changePercent24hours = changePercent24hours;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getContractStatus() {
            return contractStatus;
        }

        public void setContractStatus(String contractStatus) {
            this.contractStatus = contractStatus;
        }
    }

    private List<TickData> data = new ArrayList<>();
    private long timestamp;

    public List<TickData> getData() {
        return data;
    }

    public void setData(List<TickData> data) {
        this.data = data;
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
        private final TickerResponse r = new TickerResponse();

        public Builder data(java.util.List<TickData> v) {
            r.data = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public TickerResponse build() {
            return r;
        }
    }
}
