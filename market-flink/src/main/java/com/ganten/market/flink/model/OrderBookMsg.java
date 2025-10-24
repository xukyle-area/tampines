package com.ganten.market.flink.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderBookMsg extends MqttMsg {

    private String grouping;
    private List<List<String>> asks;
    private List<List<String>> bids;

    private Long updateId;
    private Long firstId;
    private Long lastId;
}
