package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class PlaceRequest {
    private long contractId;
    private String orderType; // common.OrderType as String
    private boolean monetary;
    private String side; // common.Side as String
    private BigDecimal totalOrderQty; // optional
    private BigDecimal totalOrderQuoteQty; // optional
    private BigDecimal price; // optional
    private String timeInForce; // common.TimeInForce as String
    private boolean postOnly;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public boolean isMonetary() {
        return monetary;
    }

    public void setMonetary(boolean monetary) {
        this.monetary = monetary;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
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

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public boolean isPostOnly() {
        return postOnly;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PlaceRequest r = new PlaceRequest();

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public Builder orderType(String v) {
            r.orderType = v;
            return this;
        }

        public Builder monetary(boolean v) {
            r.monetary = v;
            return this;
        }

        public Builder side(String v) {
            r.side = v;
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

        public Builder timeInForce(String v) {
            r.timeInForce = v;
            return this;
        }

        public Builder postOnly(boolean v) {
            r.postOnly = v;
            return this;
        }

        public PlaceRequest build() {
            return r;
        }
    }
}
