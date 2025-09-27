package com.canhxuan.product_service.dto.response;

import com.canhxuan.product_service.dto.ProductAttributeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;
    double price;
    int quantity;
    String sku;
    String status;
    CategoryResponse category;
    List<ProductAttributeDTO> attributes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
