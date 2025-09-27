package com.canhxuan.cart_service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDTO {
    Long id;
    Long productId;
    String productName;
    BigDecimal unitPrice;
    Integer quantity;
    String productImage;
    BigDecimal totalPrice;
    LocalDateTime createdAt;
}
