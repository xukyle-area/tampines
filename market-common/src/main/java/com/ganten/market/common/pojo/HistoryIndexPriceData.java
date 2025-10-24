package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class HistoryIndexPriceData {
    private String symbol;

    public static class PriceData {
        private String price;
        private String date;

        public String getPrice() {
            return price;
        }

        public void setPrice(String p) {
            price = p;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String d) {
            date = d;
        }
    }

    private List<PriceData> data = new ArrayList<>();

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<PriceData> getData() {
        return data;
    }

    public void setData(List<PriceData> data) {
        this.data = data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final HistoryIndexPriceData r = new HistoryIndexPriceData();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public Builder data(java.util.List<PriceData> v) {
            r.data = v;
            return this;
        }

        public HistoryIndexPriceData build() {
            return r;
        }
    }
}
