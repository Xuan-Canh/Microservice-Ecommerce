package com.canhxuan.cart_service.exception.customException;

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
