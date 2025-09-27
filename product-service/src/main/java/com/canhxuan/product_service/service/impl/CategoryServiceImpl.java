package com.canhxuan.product_service.service.impl;

import com.canhxuan.product_service.dto.request.CreateCategoryRequest;
import com.canhxuan.product_service.dto.request.UpdateCategoryRequest;
import com.canhxuan.product_service.dto.response.CategoryResponse;
import com.canhxuan.product_service.entity.Category;
import com.canhxuan.product_service.repository.CategoryRepository;
import com.canhxuan.product_service.service.CategoryService;
import dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryResponse toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        if (category.getChildren() != null) {
            List<CategoryResponse> childrenDto = category.getChildren().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            dto.setSubCategories(childrenDto);
        }
        dto.setCreatedAt(category.getCreated_at());
        dto.setUpdatedAt(category.getUpdated_at());
        return dto;
    }

    @Override
    public ApiResponse<List<CategoryResponse>> findAll() {
        ApiResponse<List<CategoryResponse>> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get all categories successfully");
        List<CategoryResponse> categoryResponses = categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        response.setData(categoryResponses);
        return response;
    }

    @Override
    public ApiResponse<CategoryResponse> findById(Long id) {
        ApiResponse<CategoryResponse> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get category by id " + id + " successfully");
        response.setData(toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id))));
        return response;
    }

    @Override
    public ApiResponse<CategoryResponse> create(CreateCategoryRequest request) {
        ApiResponse<CategoryResponse> response = new ApiResponse<>();
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.getById(request.getParentId()));
        } else {
            category.setParent(null);
        }
        category.setCreated_at(LocalDateTime.now());
        response.setStatus(201);
        response.setMessage("Create category successfully");
        response.setData(toDto(categoryRepository.save(category)));
        return response;
    }

    @Override
    public ApiResponse<CategoryResponse> update(Long id, UpdateCategoryRequest request) {
        ApiResponse<CategoryResponse> response = new ApiResponse<>();
        Category category = categoryRepository.findById(id).get();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.getById(request.getParentId()));
        } else {
            category.setParent(null);
        }
        category.setUpdated_at(LocalDateTime.now());
        response.setStatus(201);
        response.setMessage("Update category successfully");
        response.setData(toDto(categoryRepository.save(category)));
        return response;
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
