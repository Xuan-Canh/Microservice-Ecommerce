package com.canhxuan.order_service.controller;


import com.canhxuan.order_service.dto.OrderDTO;
import com.canhxuan.order_service.dto.request.CreateOrderRequest;
import com.canhxuan.order_service.service.OrderService;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/canhxuan/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.createOrder(request, userId, createOrderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getMyOrders(HttpServletRequest request, @RequestHeader (value = "X-User-Id", required = false) Long userId) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(orderService.getMyOrders(userId, sessionId));
    }
}
