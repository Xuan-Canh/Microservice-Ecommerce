package com.canhxuan.product_service.dto.request;

import com.canhxuan.product_service.dto.ProductAttributeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    Long categoryId;
    String name;
    String description;
    double price;
    int quantity;
    String sku;
    String status;
    List<ProductAttributeDTO> attributes;
}
