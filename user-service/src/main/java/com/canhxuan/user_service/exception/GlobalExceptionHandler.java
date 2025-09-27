package com.canhxuan.user_service.exception;

import dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(500);
        error.setMessage("Internal Server Error: " + ex.getMessage());
        error.setError(ex.getCause() != null ? ex.getCause().getMessage() : "Unknown cause error");
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(500).body(error);
    }
}
