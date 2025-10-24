package com.ganten.market.flink.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import com.ganten.market.common.model.OrderBookData;
import com.ganten.market.common.pojo.ResultEventType;
import lombok.Data;

@Data
public class DiffOrderbookEvent {

    private Long contractId;

    private ResultEventType eventType;

    private Long updateId;

    private Long firstId;

    private Long lastId;

    private final NavigableMap<BigDecimal, OrderBookData> askMap = new TreeMap<>();

    private final NavigableMap<BigDecimal, OrderBookData> bidMap = new TreeMap<>(Comparator.reverseOrder());
}
