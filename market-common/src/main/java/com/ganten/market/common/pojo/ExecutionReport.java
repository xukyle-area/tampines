package com.ganten.market.common.pojo;

public class ExecutionReport {
    private ExecutionReportType reportType;
    private long orderId;
    private String clientOrderId;

    // oneof report - nullable fields
    private TradeReport tradeReport;
    private ReplacedReport replacedReport;
    private CanceledReport canceledReport;
    private OrderStatusReport orderStatusReport;
    private CancelRejectReport cancelRejectReport;
    private ReplaceRejectReport replaceRejectReport;

    private long contractId;
    private long offset;
    private long execId;
    private long timestamp;

    public ExecutionReportType getReportType() {
        return reportType;
    }

    public void setReportType(ExecutionReportType reportType) {
        this.reportType = reportType;
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

    public TradeReport getTradeReport() {
        return tradeReport;
    }

    public void setTradeReport(TradeReport tradeReport) {
        this.tradeReport = tradeReport;
    }

    public ReplacedReport getReplacedReport() {
        return replacedReport;
    }

    public void setReplacedReport(ReplacedReport replacedReport) {
        this.replacedReport = replacedReport;
    }

    public CanceledReport getCanceledReport() {
        return canceledReport;
    }

    public void setCanceledReport(CanceledReport canceledReport) {
        this.canceledReport = canceledReport;
    }

    public OrderStatusReport getOrderStatusReport() {
        return orderStatusReport;
    }

    public void setOrderStatusReport(OrderStatusReport orderStatusReport) {
        this.orderStatusReport = orderStatusReport;
    }

    public CancelRejectReport getCancelRejectReport() {
        return cancelRejectReport;
    }

    public void setCancelRejectReport(CancelRejectReport cancelRejectReport) {
        this.cancelRejectReport = cancelRejectReport;
    }

    public ReplaceRejectReport getReplaceRejectReport() {
        return replaceRejectReport;
    }

    public void setReplaceRejectReport(ReplaceRejectReport replaceRejectReport) {
        this.replaceRejectReport = replaceRejectReport;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getExecId() {
        return execId;
    }

    public void setExecId(long execId) {
        this.execId = execId;
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
        private final ExecutionReport r = new ExecutionReport();

        public Builder reportType(ExecutionReportType v) {
            r.reportType = v;
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

        public Builder tradeReport(TradeReport v) {
            r.tradeReport = v;
            return this;
        }

        public Builder replacedReport(ReplacedReport v) {
            r.replacedReport = v;
            return this;
        }

        public Builder canceledReport(CanceledReport v) {
            r.canceledReport = v;
            return this;
        }

        public Builder orderStatusReport(OrderStatusReport v) {
            r.orderStatusReport = v;
            return this;
        }

        public Builder cancelRejectReport(CancelRejectReport v) {
            r.cancelRejectReport = v;
            return this;
        }

        public Builder replaceRejectReport(ReplaceRejectReport v) {
            r.replaceRejectReport = v;
            return this;
        }

        public Builder contractId(long v) {
            r.contractId = v;
            return this;
        }

        public Builder offset(long v) {
            r.offset = v;
            return this;
        }

        public Builder execId(long v) {
            r.execId = v;
            return this;
        }

        public Builder timestamp(long v) {
            r.timestamp = v;
            return this;
        }

        public ExecutionReport build() {
            return r;
        }
    }
}
