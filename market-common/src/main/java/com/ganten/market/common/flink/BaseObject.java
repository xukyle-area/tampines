package com.ganten.market.common.flink;


import lombok.Data;

@Data
public abstract class BaseObject {
    protected long contractId;
    protected long timestamp = System.currentTimeMillis();
}
