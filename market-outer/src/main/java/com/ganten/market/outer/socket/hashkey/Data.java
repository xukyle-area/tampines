package com.ganten.market.outer.socket.hashkey;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Data {

    @JsonProperty("t")
    private long timestamp;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("sn")
    private String symbolName;

    @JsonProperty("c")
    private String close;

    @JsonProperty("h")
    private String high;

    @JsonProperty("l")
    private String low;

    @JsonProperty("o")
    private String open;

    @JsonProperty("v")
    private String volume;

    @JsonProperty("qv")
    private String quoteVolume;

    @JsonProperty("m")
    private String change;

    @JsonProperty("e")
    private int exchange;

}
