package com.ganten.market.common.model;

import java.math.BigDecimal;

public class OrderBookData {

    private BigDecimal quantity;
    private BigDecimal price;

    public OrderBookData() {}

    public OrderBookData(BigDecimal price, BigDecimal quantity) {
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
