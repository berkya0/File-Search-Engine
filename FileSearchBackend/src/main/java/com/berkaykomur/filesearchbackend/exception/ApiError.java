package com.berkaykomur.filesearchbackend.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {
    private final LocalDateTime errorTime;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
}
