package com.canhxuan.cart_service.exception;

import dto.ApiResponse;
import dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("An unexpected error occurred: " + ex.getMessage());
        error.setError(ex.getCause() != null ? ex.getCause().getMessage() : "Unknown error");
        error.setTimestamp(LocalDateTime.now());
        return error;
    }
}
