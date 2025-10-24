package com.ganten.market.common.pojo;

public class CanceledReport {
    private String execId; // deprecated

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CanceledReport r = new CanceledReport();

        public Builder execId(String v) {
            r.execId = v;
            return this;
        }

        public CanceledReport build() {
            return r;
        }
    }
}
