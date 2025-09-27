package com.canhxuan.order_service.client;

import com.canhxuan.order_service.dto.CartDTO;
import dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service", url = "http://localhost:8085/canhxuan/cart")
public interface CartServiceClient {

    @GetMapping
    ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestHeader(value = "X-Session-Id") String sessionId);

    @DeleteMapping
    ResponseEntity<ApiResponse<CartDTO>> clearCart(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                   @RequestHeader(value = "X-Session-Id") String sessionId);
}
