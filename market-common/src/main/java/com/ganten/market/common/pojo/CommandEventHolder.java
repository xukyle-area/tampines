package com.ganten.market.common.pojo;

public class CommandEventHolder {
    private CommandEventType eventType;

    // oneof event
    private OrderOperationRequest orderOperationRequest; // type exists elsewhere in pojo folder
    private SnapshotCommand snapshotCommand;
    private OrderBookSyncCommand orderBookSyncCommand;
    private StatusTransitionCommand statusTransitionCommand;

    private long offset;

    public CommandEventType getEventType() {
        return eventType;
    }

    public void setEventType(CommandEventType eventType) {
        this.eventType = eventType;
    }

    public OrderOperationRequest getOrderOperationRequest() {
        return orderOperationRequest;
    }

    public void setOrderOperationRequest(OrderOperationRequest orderOperationRequest) {
        this.orderOperationRequest = orderOperationRequest;
    }

    public SnapshotCommand getSnapshotCommand() {
        return snapshotCommand;
    }

    public void setSnapshotCommand(SnapshotCommand snapshotCommand) {
        this.snapshotCommand = snapshotCommand;
    }

    public OrderBookSyncCommand getOrderBookSyncCommand() {
        return orderBookSyncCommand;
    }

    public void setOrderBookSyncCommand(OrderBookSyncCommand orderBookSyncCommand) {
        this.orderBookSyncCommand = orderBookSyncCommand;
    }

    public StatusTransitionCommand getStatusTransitionCommand() {
        return statusTransitionCommand;
    }

    public void setStatusTransitionCommand(StatusTransitionCommand statusTransitionCommand) {
        this.statusTransitionCommand = statusTransitionCommand;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CommandEventHolder r = new CommandEventHolder();

        public Builder eventType(CommandEventType v) {
            r.eventType = v;
            return this;
        }

        public Builder orderOperationRequest(OrderOperationRequest v) {
            r.orderOperationRequest = v;
            return this;
        }

        public Builder snapshotCommand(SnapshotCommand v) {
            r.snapshotCommand = v;
            return this;
        }

        public Builder orderBookSyncCommand(OrderBookSyncCommand v) {
            r.orderBookSyncCommand = v;
            return this;
        }

        public Builder statusTransitionCommand(StatusTransitionCommand v) {
            r.statusTransitionCommand = v;
            return this;
        }

        public Builder offset(long v) {
            r.offset = v;
            return this;
        }

        public CommandEventHolder build() {
            return r;
        }
    }
}
