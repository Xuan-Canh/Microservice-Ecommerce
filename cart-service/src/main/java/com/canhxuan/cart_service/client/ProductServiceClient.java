package com.canhxuan.cart_service.client;

import com.canhxuan.cart_service.dto.ProductDTO;
import dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "product-service", url = "http://localhost:8081")
public interface ProductServiceClient {

    @GetMapping("/canhxuan/products/{id}")
    ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable("id") Long id);

}
