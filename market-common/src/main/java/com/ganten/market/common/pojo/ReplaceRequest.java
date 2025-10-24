package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class ReplaceRequest {
    private long contractId;
    private BigDecimal totalOrderQty;
    private BigDecimal totalOrderQuoteQty;
    private BigDecimal price;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public BigDecimal getTotalOrderQty() {
        return totalOrderQty;
    }

    public void setTotalOrderQty(BigDecimal totalOrderQty) {
        this.totalOrderQty = totalOrderQty;
    }

    public BigDecimal getTotalOrderQuoteQty() {
        return totalOrderQuoteQty;
    }

    public void setTotalOrderQuoteQty(BigDecimal totalOrderQuoteQty) {
        this.totalOrderQuoteQty = totalOrderQuoteQty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ReplaceRequest r = new ReplaceRequest();

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public Builder totalOrderQty(BigDecimal v) {
            r.totalOrderQty = v;
            return this;
        }

        public Builder totalOrderQuoteQty(BigDecimal v) {
            r.totalOrderQuoteQty = v;
            return this;
        }

        public Builder price(BigDecimal v) {
            r.price = v;
            return this;
        }

        public ReplaceRequest build() {
            return r;
        }
    }
}
