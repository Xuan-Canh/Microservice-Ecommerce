package com.canhxuan.cart_service.client;

import com.canhxuan.cart_service.dto.ProductDTO;
import dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClientFallback implements ProductServiceClient{
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClientFallback.class);

    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(Long id) {
        logger.warn("Fallback: Cannot get product with id: {}", id);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
