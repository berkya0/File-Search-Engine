package com.berkaykomur.filesearchbackend.exception;

import org.springframework.http.HttpStatus;

public class FileOrDirectoryNotFound extends BaseException {
    public FileOrDirectoryNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
