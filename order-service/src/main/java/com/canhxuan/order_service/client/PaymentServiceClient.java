package com.canhxuan.order_service.client;

import com.canhxuan.order_service.dto.request.PaymentRequest;
import dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8087/canhxuan/payment")
public interface PaymentServiceClient {

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPayment(@RequestBody PaymentRequest paymentRequest);
}
