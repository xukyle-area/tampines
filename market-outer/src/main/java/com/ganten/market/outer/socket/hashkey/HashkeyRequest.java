package com.ganten.market.outer.socket.hashkey;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HashkeyRequest {
    private String symbol;
    private String topic = "realtimes";
    private String event = "sub";
    private Map<String, Object> params;
    private long id;
}
