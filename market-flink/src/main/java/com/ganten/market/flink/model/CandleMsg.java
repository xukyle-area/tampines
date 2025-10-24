package com.ganten.market.flink.model;


public class CandleMsg extends MqttMsg {

    private Integer resolution;
    private Long startTime;
    private String open;
    private String close;
    private String high;
    private String volume;

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    private String low;

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
