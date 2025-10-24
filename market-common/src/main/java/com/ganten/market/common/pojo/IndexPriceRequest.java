package com.ganten.market.common.pojo;

public class IndexPriceRequest {
    private long contractId;
    private String symbol;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long v) {
        this.contractId = v;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String s) {
        this.symbol = s;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final IndexPriceRequest r = new IndexPriceRequest();

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public Builder symbol(String v) {
            r.symbol = v;
            return this;
        }

        public IndexPriceRequest build() {
            return r;
        }
    }
}
