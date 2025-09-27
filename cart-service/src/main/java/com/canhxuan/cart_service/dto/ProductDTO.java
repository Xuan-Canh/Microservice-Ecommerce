package com.canhxuan.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    int id;
    String name;
    String description;
    BigDecimal price;
    int quantity;
    String sku;
    String status;
    String imageUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
