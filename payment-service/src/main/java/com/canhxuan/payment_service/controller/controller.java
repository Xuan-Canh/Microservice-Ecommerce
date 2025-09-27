package com.canhxuan.payment_service.controller;

import com.canhxuan.payment_service.dto.request.PaymentRequest;
import com.canhxuan.payment_service.dto.response.PaymentResponse;
import com.canhxuan.payment_service.entity.PaymentStatus;
import com.canhxuan.payment_service.service.PaymentService;
import dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("canhxuan/payment")
public class controller {

    private final PaymentService paymentService;

    public controller(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePayment(@PathVariable Integer paymentId, @RequestBody PaymentStatus paymentStatus) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, paymentStatus));
    }
}
