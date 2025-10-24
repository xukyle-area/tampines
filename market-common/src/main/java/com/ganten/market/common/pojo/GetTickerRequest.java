package com.ganten.market.common.pojo;

public class GetTickerRequest {
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GetTickerRequest r = new GetTickerRequest();

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public GetTickerRequest build() {
            return r;
        }
    }
}
