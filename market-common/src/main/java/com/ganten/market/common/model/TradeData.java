package com.ganten.market.common.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class TradeData {
    private Long contractId;
    private Long id;
    private String price;
    private String volume;
    private Long timestamp;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isWithin24Hours() {
        final Instant now = Instant.now();
        final Instant tradeTime = Instant.ofEpochMilli(timestamp);
        return tradeTime.isAfter(now.minus(24, ChronoUnit.HOURS));
    }

    @Override
    public String toString() {
        final ToStringHelper helper = MoreObjects.toStringHelper(this).omitNullValues();
        helper.add("contractId", contractId).add("id", id).add("price", price).add("volume", volume).add("timestamp",
                timestamp);
        return helper.toString();
    }


}
