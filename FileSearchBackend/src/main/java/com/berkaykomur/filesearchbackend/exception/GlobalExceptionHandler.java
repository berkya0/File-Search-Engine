package com.berkaykomur.filesearchbackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleDirectoryAlreadyWatched(BaseException e, WebRequest request) {
        log.warn("Uygulama içinde bir hatalı istek yakalandı {}", e.getMessage());

        String cleanPath = request.getDescription(false).replace("uri=", "");
        ApiError apiError= ApiError.builder()
                .errorTime(LocalDateTime.now())
                .status(e.getHttpStatus().value())
                .error(e.getClass().getSimpleName())
                .message(e.getMessage())
                .path(cleanPath)
                .build();
        return ResponseEntity.status(e.getHttpStatus()).body(apiError);

    }
}
