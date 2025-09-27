package com.canhxuan.product_service.dto.response;

import com.canhxuan.product_service.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String name;
    String description;
    List<CategoryResponse> subCategories;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
