package com.canhxuan.order_service.service;

import com.canhxuan.order_service.dto.OrderDTO;
import com.canhxuan.order_service.dto.request.CreateOrderRequest;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    ApiResponse<OrderDTO> createOrder(HttpServletRequest request, Long userId, CreateOrderRequest createOrderRequest);
    ApiResponse<OrderDTO> getOrderById(Long orderId);
    ApiResponse<List<OrderDTO>> getMyOrders(Long userId, String sessionId);
}
