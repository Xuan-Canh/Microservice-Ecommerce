package com.canhxuan.order_service.client;

import com.canhxuan.order_service.dto.request.InventoryReservationRequest;
import com.canhxuan.order_service.dto.response.InventoryReservationResponse;
import dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8081/canhxuan/products")
public interface ProductServiceClient {

    @PutMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryReservationResponse>> reserveInventory(@RequestBody InventoryReservationRequest request);

    @PutMapping("/release")
    public ResponseEntity<ApiResponse<?>> releaseInventory(@RequestBody List<String> reservationId);
}
