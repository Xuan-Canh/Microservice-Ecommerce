package com.canhxuan.product_service.service;

import com.canhxuan.product_service.dto.request.CreateCategoryRequest;
import com.canhxuan.product_service.dto.request.UpdateCategoryRequest;
import com.canhxuan.product_service.dto.response.CategoryResponse;
import dto.ApiResponse;

import java.util.List;

public interface CategoryService {
    ApiResponse<List<CategoryResponse>> findAll();
    ApiResponse<CategoryResponse> findById(Long id);
    ApiResponse<CategoryResponse> create(CreateCategoryRequest request);
    ApiResponse<CategoryResponse> update(Long id, UpdateCategoryRequest request);
    void delete(Long id);
}
