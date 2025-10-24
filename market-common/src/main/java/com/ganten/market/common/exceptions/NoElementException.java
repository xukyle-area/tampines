package com.ganten.market.common.exceptions;

public class NoElementException extends OtherMarketException {

    private static final long serialVersionUID = 5829587822227812607L;

    public NoElementException(String message) {
        super(message);
    }

    public NoElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoElementException(Throwable cause) {
        super(cause);
    }

}
