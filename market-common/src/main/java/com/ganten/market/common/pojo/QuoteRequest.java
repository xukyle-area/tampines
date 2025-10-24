package com.ganten.market.common.pojo;

public class QuoteRequest {
    private long contractId;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long v) {
        this.contractId = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuoteRequest r = new QuoteRequest();

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public QuoteRequest build() {
            return r;
        }
    }
}
