package com.berkaykomur.filesearchbackend.exception;

import org.springframework.http.HttpStatus;

public class DirectoryAlreadyWatched extends BaseException {
    public DirectoryAlreadyWatched(String message) {
        super(message, HttpStatus.CONFLICT);

    }
}
