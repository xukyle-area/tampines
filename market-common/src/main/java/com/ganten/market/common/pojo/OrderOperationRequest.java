package com.ganten.market.common.pojo;

public class OrderOperationRequest {
    private OrderOperationType operationType;
    private long orderId;
    private String clientOrderId;

    // oneof request
    private PlaceRequest placeRequest;
    private ReplaceRequest replaceRequest;
    private CancelRequest cancelRequest;
    private StatusTransitionRequest statusTransitionRequest;
    private ChangeOrderLimitPercentileRequest changeOrderLimitPercentileRequest;

    private long accountId;
    private long offset;
    private long timestamp;

    public OrderOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OrderOperationType operationType) {
        this.operationType = operationType;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public void setPlaceRequest(PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    public ReplaceRequest getReplaceRequest() {
        return replaceRequest;
    }

    public void setReplaceRequest(ReplaceRequest replaceRequest) {
        this.replaceRequest = replaceRequest;
    }

    public CancelRequest getCancelRequest() {
        return cancelRequest;
    }

    public void setCancelRequest(CancelRequest cancelRequest) {
        this.cancelRequest = cancelRequest;
    }

    public StatusTransitionRequest getStatusTransitionRequest() {
        return statusTransitionRequest;
    }

    public void setStatusTransitionRequest(StatusTransitionRequest statusTransitionRequest) {
        this.statusTransitionRequest = statusTransitionRequest;
    }

    public ChangeOrderLimitPercentileRequest getChangeOrderLimitPercentileRequest() {
        return changeOrderLimitPercentileRequest;
    }

    public void setChangeOrderLimitPercentileRequest(
            ChangeOrderLimitPercentileRequest changeOrderLimitPercentileRequest) {
        this.changeOrderLimitPercentileRequest = changeOrderLimitPercentileRequest;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderOperationRequest r = new OrderOperationRequest();

        public Builder operationType(OrderOperationType v) {
            r.operationType = v;
            return this;
        }

        public Builder orderId(long v) {
            r.orderId = v;
            return this;
        }

        public Builder clientOrderId(String v) {
            r.clientOrderId = v;
            return this;
        }

        public Builder placeRequest(PlaceRequest v) {
            r.placeRequest = v;
            return this;
        }

        public Builder replaceRequest(ReplaceRequest v) {
            r.replaceRequest = v;
            return this;
        }

        public Builder cancelRequest(CancelRequest v) {
            r.cancelRequest = v;
            return this;
        }

        public Builder statusTransitionRequest(StatusTransitionRequest v) {
            r.statusTransitionRequest = v;
            return this;
        }

        public Builder changeOrderLimitPercentileRequest(ChangeOrderLimitPercentileRequest v) {
            r.changeOrderLimitPercentileRequest = v;
            return this;
        }

        public Builder accountId(long v) {
            r.accountId = v;
            return this;
        }

        public Builder offset(long v) {
            r.offset = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public OrderOperationRequest build() {
            return r;
        }
    }
}
