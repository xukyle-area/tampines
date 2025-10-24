package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class OrderBookTuple {
    private BigDecimal price;
    private BigDecimal quantity;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal p) {
        price = p;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal q) {
        quantity = q;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderBookTuple r = new OrderBookTuple();

        public Builder price(BigDecimal v) {
            r.price = v;
            return this;
        }

        public Builder quantity(BigDecimal v) {
            r.quantity = v;
            return this;
        }

        public OrderBookTuple build() {
            return r;
        }
    }
}
