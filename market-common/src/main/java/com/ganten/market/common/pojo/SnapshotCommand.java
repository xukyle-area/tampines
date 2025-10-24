package com.ganten.market.common.pojo;

public class SnapshotCommand {
    private long timestamp;

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
        private final SnapshotCommand r = new SnapshotCommand();

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public SnapshotCommand build() {
            return r;
        }
    }
}
