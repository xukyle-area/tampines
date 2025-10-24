package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class TickerPaginationResponse {
    private int curPage;
    private int totalPage;
    private List<TickerResponse.TickData> data = new ArrayList<>();
    private long timestamp;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<TickerResponse.TickData> getData() {
        return data;
    }

    public void setData(List<TickerResponse.TickData> data) {
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
        private final TickerPaginationResponse r = new TickerPaginationResponse();

        public Builder curPage(int v) {
            r.curPage = v;
            return this;
        }

        public Builder totalPage(int v) {
            r.totalPage = v;
            return this;
        }

        public Builder data(java.util.List<TickerResponse.TickData> v) {
            r.data = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public TickerPaginationResponse build() {
            return r;
        }
    }
}
