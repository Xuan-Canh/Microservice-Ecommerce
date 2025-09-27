package com.canhxuan.order_service.entity;

public enum OrderStatus {
    PENDING("Pending", "Order created, awaiting payment"),
    PAYMENT_PROCESSING("Payment Processing", "Payment is being processed"),
    CONFIRMED("Confirmed", "Payment successful, order confirmed"),
    PROCESSING("Processing", "Order is being prepared"),
    SHIPPED("Shipped", "Order has been shipped"),
    DELIVERED("Delivered", "Order delivered successfully"),
    CANCELLED("Cancelled", "Order has been cancelled"),
    PAYMENT_FAILED("Payment Failed", "Payment processing failed"),
    REFUNDED("Refunded", "Order has been refunded");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // Business logic methods
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED || this == PROCESSING;
    }

    public boolean isRefundable() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }

    public boolean requiresPayment() {
        return this == PENDING || this == PAYMENT_PROCESSING;
    }
}