package com.ganten.market.flink.model;

public class TickMsg extends MqttMsg {

    private String ask;
    private String bid;
    private String last;
    private String change24hours;
    private String changePercent24hours;
    private String highest24hours;
    private String lowest24hours;
    private String volume;

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getChange24hours() {
        return change24hours;
    }

    public void setChange24hours(String change24hours) {
        this.change24hours = change24hours;
    }

    public String getChangePercent24hours() {
        return changePercent24hours;
    }

    public void setChangePercent24hours(String changePercent24hours) {
        this.changePercent24hours = changePercent24hours;
    }

    public String getHighest24hours() {
        return highest24hours;
    }

    public void setHighest24hours(String highest24hours) {
        this.highest24hours = highest24hours;
    }

    public String getLowest24hours() {
        return lowest24hours;
    }

    public void setLowest24hours(String lowest24hours) {
        this.lowest24hours = lowest24hours;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
