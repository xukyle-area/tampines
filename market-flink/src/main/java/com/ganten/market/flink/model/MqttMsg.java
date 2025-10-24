package com.ganten.market.flink.model;

public abstract class MqttMsg {

    private Long timestamp = System.currentTimeMillis();
    private String symbol;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
