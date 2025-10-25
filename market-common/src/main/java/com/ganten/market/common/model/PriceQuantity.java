package com.ganten.market.common.model;

import java.math.BigDecimal;

public class PriceQuantity {

    private BigDecimal quantity;
    private BigDecimal price;

    public PriceQuantity() {}

    public PriceQuantity(BigDecimal price, BigDecimal quantity) {
        this.quantity = quantity;
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
