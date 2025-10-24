package com.ganten.market.common.pojo;

public class GetTickerPaginationRequest {
    private String secType;
    private int pageNo;
    private int pageSize;
    private String symbolContains;

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSymbolContains() {
        return symbolContains;
    }

    public void setSymbolContains(String symbolContains) {
        this.symbolContains = symbolContains;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GetTickerPaginationRequest r = new GetTickerPaginationRequest();

        public Builder secType(String v) {
            r.secType = v;
            return this;
        }

        public Builder pageNo(int v) {
            r.pageNo = v;
            return this;
        }

        public Builder pageSize(int v) {
            r.pageSize = v;
            return this;
        }

        public Builder symbolContains(String v) {
            r.symbolContains = v;
            return this;
        }

        public GetTickerPaginationRequest build() {
            return r;
        }
    }
}
