package com.ganten.market.outer.socket.cryptocom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CryptoEvent {

    private String code;

    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result {
        private String subscription;

        private Dat[] data;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Dat {
        @JsonProperty("k")
        private String ask;
        @JsonProperty("b")
        private String bid;
        @JsonProperty("a")
        private String last;
    }
}


