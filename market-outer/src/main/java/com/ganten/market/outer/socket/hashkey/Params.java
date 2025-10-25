package com.ganten.market.outer.socket.hashkey;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Params {
    private String realtimeInterval;
    private String binary;
}
