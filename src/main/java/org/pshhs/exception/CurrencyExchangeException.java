package org.pshhs.exception;

import lombok.Getter;

@Getter
public class CurrencyExchangeException extends Exception {
    private final int errorCode;

    public CurrencyExchangeException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

