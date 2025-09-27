package com.canhxuan.payment_service.service;

import com.canhxuan.payment_service.dto.request.PaymentRequest;
import com.canhxuan.payment_service.dto.response.PaymentResponse;
import com.canhxuan.payment_service.entity.PaymentStatus;
import dto.ApiResponse;

public interface PaymentService {
    ApiResponse<PaymentResponse> createPayment(PaymentRequest paymentRequest);
    ApiResponse<PaymentResponse> updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus);
}
