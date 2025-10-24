package com.ganten.market.common.pojo;

public class CancelRequest {
    private long contractId;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CancelRequest r = new CancelRequest();

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public CancelRequest build() {
            return r;
        }
    }
}
