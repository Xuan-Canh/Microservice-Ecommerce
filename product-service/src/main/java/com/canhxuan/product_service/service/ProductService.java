package com.canhxuan.product_service.service;

import com.canhxuan.product_service.dto.request.CreateProductRequest;
import com.canhxuan.product_service.dto.request.InventoryReservationRequest;
import com.canhxuan.product_service.dto.request.UpdateProductRequest;
import com.canhxuan.product_service.dto.response.InventoryReservationResponse;
import com.canhxuan.product_service.dto.response.ProductResponse;
import dto.ApiResponse;

import java.util.List;

public interface ProductService {
    ApiResponse<ProductResponse> getById(Long id);

    ApiResponse<List<ProductResponse>> getAll();

    ApiResponse<ProductResponse> create(CreateProductRequest request);

    ApiResponse<ProductResponse> update(Long id, UpdateProductRequest request);

    ApiResponse<?> delete(Long id);

    ApiResponse<InventoryReservationResponse> reserveInventory(InventoryReservationRequest request);

    ApiResponse<?> releaseInventory(List<String> reservationId);
}
