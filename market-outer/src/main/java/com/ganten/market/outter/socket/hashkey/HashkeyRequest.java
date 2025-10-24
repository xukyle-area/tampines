package com.ganten.market.outter.socket.hashkey;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashkeyRequest {
    private String symbol;
    private String topic = "realtimes";
    private String event = "sub";
    private Map<String, Object> params;
    private long id;
}
