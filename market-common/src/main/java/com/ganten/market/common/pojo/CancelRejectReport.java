package com.ganten.market.common.pojo;

public class CancelRejectReport {
    private String orderStatus; // store as String
    private String reason;
    private int errorCode;
    private String debugInfo;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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
        private final CancelRejectReport r = new CancelRejectReport();

        public Builder orderStatus(String v) {
            r.orderStatus = v;
            return this;
        }

        public Builder reason(String v) {
            r.reason = v;
            return this;
        }

        public Builder errorCode(int v) {
            r.errorCode = v;
            return this;
        }

        public Builder debugInfo(String v) {
            r.debugInfo = v;
            return this;
        }

        public CancelRejectReport build() {
            return r;
        }
    }
}
