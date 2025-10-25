package com.ganten.market.outer.socket.cryptocom;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CryptoRequest {
    private Long id;
    private String method;
    private Map<String, Object> params;
    private Long nonce;
}
