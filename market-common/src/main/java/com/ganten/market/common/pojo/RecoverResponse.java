package com.ganten.market.common.pojo;

public class RecoverResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RecoverResponse r = new RecoverResponse();

        public Builder message(String v) {
            r.message = v;
            return this;
        }

        public RecoverResponse build() {
            return r;
        }
    }
}
