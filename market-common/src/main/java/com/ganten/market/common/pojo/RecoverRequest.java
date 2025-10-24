package com.ganten.market.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class RecoverRequest {
    private String type;
    private long endTime;
    private List<Long> contractIds = new ArrayList<>();
    private long startTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<Long> getContractIds() {
        return contractIds;
    }

    public void setContractIds(List<Long> contractIds) {
        this.contractIds = contractIds;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RecoverRequest r = new RecoverRequest();

        public Builder type(String v) {
            r.type = v;
            return this;
        }

        public Builder endTime(long v) {
            r.endTime = v;
            return this;
        }

        public Builder contractIds(java.util.List<Long> v) {
            r.contractIds = v;
            return this;
        }

        public Builder startTime(long v) {
            r.startTime = v;
            return this;
        }

        public RecoverRequest build() {
            return r;
        }
    }
}
