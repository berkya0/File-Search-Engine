package com.berkaykomur.filesearchbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    @Getter
    private final HttpStatus httpStatus;
    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;

    }
}
