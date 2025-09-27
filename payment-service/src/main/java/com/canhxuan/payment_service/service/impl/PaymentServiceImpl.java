package com.canhxuan.payment_service.service.impl;

import com.canhxuan.payment_service.dto.request.PaymentRequest;
import com.canhxuan.payment_service.dto.response.PaymentResponse;
import com.canhxuan.payment_service.entity.Payment;
import com.canhxuan.payment_service.entity.PaymentStatus;
import com.canhxuan.payment_service.repository.PaymentRepository;
import com.canhxuan.payment_service.service.PaymentService;
import dto.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public ApiResponse<PaymentResponse> createPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setUserId(paymentRequest.getUserId());
        payment.setSessionId(paymentRequest.getSessionId());
        payment.setMethod(paymentRequest.getMethod());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        // call payment gateway here
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage("Payment created successfully");
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setOrderId(payment.getOrderId());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setStatus(payment.getStatus());
        apiResponse.setData(paymentResponse);
        return apiResponse;
    }

    @Override
    public ApiResponse<PaymentResponse> updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id " + paymentId));
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage("Payment status updated successfully");
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setOrderId(payment.getOrderId());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setStatus(payment.getStatus());
        apiResponse.setData(paymentResponse);
        return apiResponse;
    }


}
