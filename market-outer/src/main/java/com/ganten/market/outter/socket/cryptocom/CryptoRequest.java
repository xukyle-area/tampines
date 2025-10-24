package com.ganten.market.outter.socket.cryptocom;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRequest {
    private Long id;
    private String method;
    private Map<String, Object> params;
    private Long nonce;
}
