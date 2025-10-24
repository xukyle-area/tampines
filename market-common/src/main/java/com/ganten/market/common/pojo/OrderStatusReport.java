package com.ganten.market.common.pojo;

import java.math.BigDecimal;

public class OrderStatusReport {
    private String execId; // deprecated
    private String orderStatus; // store as String for compatibility
    private BigDecimal totalOrderQty;
    private BigDecimal totalOrderQuoteQty;
    private BigDecimal price;
    private BigDecimal cumFilledOrderQty;
    private BigDecimal cumFilledOrderQuoteQty;
    private int errorCode;
    private String reason;
    private String debugInfo;

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public BigDecimal getCumFilledOrderQty() {
        return cumFilledOrderQty;
    }

    public void setCumFilledOrderQty(BigDecimal cumFilledOrderQty) {
        this.cumFilledOrderQty = cumFilledOrderQty;
    }

    public BigDecimal getCumFilledOrderQuoteQty() {
        return cumFilledOrderQuoteQty;
    }

    public void setCumFilledOrderQuoteQty(BigDecimal cumFilledOrderQuoteQty) {
        this.cumFilledOrderQuoteQty = cumFilledOrderQuoteQty;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderStatusReport r = new OrderStatusReport();

        public Builder execId(String v) {
            r.execId = v;
            return this;
        }

        public Builder orderStatus(String v) {
            r.orderStatus = v;
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

        public Builder cumFilledOrderQty(BigDecimal v) {
            r.cumFilledOrderQty = v;
            return this;
        }

        public Builder cumFilledOrderQuoteQty(BigDecimal v) {
            r.cumFilledOrderQuoteQty = v;
            return this;
        }

        public Builder errorCode(int v) {
            r.errorCode = v;
            return this;
        }

        public Builder reason(String v) {
            r.reason = v;
            return this;
        }

        public Builder debugInfo(String v) {
            r.debugInfo = v;
            return this;
        }

        public OrderStatusReport build() {
            return r;
        }
    }
}
