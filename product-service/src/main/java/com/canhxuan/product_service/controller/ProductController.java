package com.canhxuan.product_service.controller;

import com.canhxuan.product_service.dto.request.CreateProductRequest;
import com.canhxuan.product_service.dto.request.InventoryReservationRequest;
import com.canhxuan.product_service.dto.request.UpdateProductRequest;
import com.canhxuan.product_service.dto.response.InventoryReservationResponse;
import com.canhxuan.product_service.dto.response.ProductResponse;
import com.canhxuan.product_service.service.ProductService;
import dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/canhxuan/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.delete(id));
    }

    @PutMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryReservationResponse>> reserveInventory(@RequestBody InventoryReservationRequest request) {
        return ResponseEntity.ok(productService.reserveInventory(request));
    }

    @PutMapping("/release")
    public ResponseEntity<ApiResponse<?>> releaseInventory(@RequestBody List<String> reservationIds) {
        return ResponseEntity.ok(productService.releaseInventory(reservationIds));
    }
}
