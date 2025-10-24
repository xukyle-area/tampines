package com.ganten.market.common.exceptions;

public class OtherMarketException extends Exception {

    private static final long serialVersionUID = 2233103790726141331L;

    public OtherMarketException(String message) {
        super(message);
    }

    public OtherMarketException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtherMarketException(Throwable cause) {
        super(cause);
    }

}
