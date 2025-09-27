package com.canhxuan.order_service.service.impl;

import com.canhxuan.order_service.client.CartServiceClient;
import com.canhxuan.order_service.client.PaymentServiceClient;
import com.canhxuan.order_service.client.ProductServiceClient;
import com.canhxuan.order_service.dto.CartDTO;
import com.canhxuan.order_service.dto.CartItemDTO;
import com.canhxuan.order_service.dto.OrderDTO;
import com.canhxuan.order_service.dto.OrderItemDTO;
import com.canhxuan.order_service.dto.request.CreateOrderRequest;
import com.canhxuan.order_service.dto.request.InventoryReservationRequest;
import com.canhxuan.order_service.dto.request.PaymentRequest;
import com.canhxuan.order_service.dto.response.InventoryReservationResponse;
import com.canhxuan.order_service.entity.Order;
import com.canhxuan.order_service.entity.OrderItem;
import com.canhxuan.order_service.entity.OrderStatus;
import com.canhxuan.order_service.repository.OrderRepository;
import com.canhxuan.order_service.service.OrderService;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository, CartServiceClient cartServiceClient, ProductServiceClient productServiceClient, PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.cartServiceClient = cartServiceClient;
        this.productServiceClient = productServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }


    @Override
    public ApiResponse<OrderDTO> createOrder(HttpServletRequest request, Long userId, CreateOrderRequest createOrderRequest) {
        log.info("Creating order for userId: {}, sessionId: {}", userId, request.getSession().getId());
        String sessionId = request.getSession().getId();
        try {
            validateCreateOrderRequest(createOrderRequest);
            CartDTO cart = validateAndGetCart(userId, sessionId);
            validateCartItem(cart);
            List<String> reservationIds = reserveStock(cart.getItems());
            try {
                Order order = new Order();
                order.setUserId(userId);
                order.setSessionId(sessionId);
                order.setShippingAddress(createOrderRequest.getShippingAddress());
                order.setPaymentMethod(createOrderRequest.getPaymentMethod());
                order.setTotalAmount(cart.getTotalAmount());
                order.setOrderStatus(OrderStatus.PENDING);
                List<OrderItem> orderItems = toOrderItem(cart.getItems(), order);
                order.setOrderItems(orderItems);
                orderRepository.save(order);
                PaymentRequest paymentRequest = new PaymentRequest(order.getId(), order.getUserId(), order.getSessionId(), order.getTotalAmount(), order.getPaymentMethod());
                paymentServiceClient.createPayment(paymentRequest);
                cartServiceClient.clearCart(userId, sessionId);
                OrderDTO orderDTO = toOrderDTO(order);
                ApiResponse<OrderDTO> response = new ApiResponse<>();
                response.setStatus(201);
                response.setMessage("Create order successfully");
                response.setData(orderDTO);
                log.info("Create order successfully");
                return response;
            } catch (Exception e) {
                log.error("Error saving order for userId: {}, sessionId: {}: {}", userId, sessionId, e.getMessage());
                rollbackReservations(reservationIds);
                throw new IllegalArgumentException("Failed to create order: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error creating order for userId: {}, sessionId: {}: {}", userId, sessionId, e.getMessage());
            throw new IllegalArgumentException("Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<OrderDTO> getOrderById(Long orderId) {
        log.debug("Get order by orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        OrderDTO orderDTO = toOrderDTO(order);
        ApiResponse<OrderDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get order successfully");
        response.setData(orderDTO);
        return response;
    }

    @Override
    public ApiResponse<List<OrderDTO>> getMyOrders(Long userId, String sessionId) {
        List<Order> orders = findMyOrders(userId, sessionId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = toOrderDTO(order);
            orderDTOs.add(orderDTO);
        }
        ApiResponse<List<OrderDTO>> response = new ApiResponse<>();
        response.setStatus(200);
        if (orderDTOs.isEmpty()) {
            response.setMessage("No orders found");
        } else {
            response.setMessage("Get my orders successfully");
        }
        response.setData(orderDTOs);
        return response;
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request.getShippingAddress() == null || request.getShippingAddress().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }
    }

    private CartDTO validateAndGetCart(Long userId, String sessionId) {
        log.debug("Validating cart for userId: {}, sessionId: {}", userId, sessionId);
        ApiResponse<CartDTO> cartResponse = cartServiceClient.getCart(userId, sessionId).getBody();
        if (cartResponse == null || cartResponse.getData() == null) {
            throw new IllegalArgumentException("Unable to retrieve cart");
        }

        CartDTO cartDTO = cartResponse.getData();

        if (cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        if (cartDTO.getTotalAmount() == null || cartDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid cart total amount");
        }

        log.debug("Cart validated successfully for userId: {}, sessionId: {}", userId, sessionId);
        return cartDTO;
    }


    private void validateCartItem(CartDTO cartDTO) {
        log.debug("Validating {} cart items", cartDTO.getItems().size());
        for (CartItemDTO itemDTO : cartDTO.getItems()) {
            if (itemDTO.getProductId() == null || itemDTO.getProductId() <= 0) {
                throw new IllegalArgumentException("Invalid cart item productId: " + itemDTO.getProductId());
            }
            if (itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid cart item quantity for productId: " + itemDTO.getProductId());
            }

            if (itemDTO.getUnitPrice() == null || itemDTO.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid cart item unit price for productId: " + itemDTO.getProductId());
            }
        }
        log.debug("All cart items are valid");
    }

    private List<String> reserveStock(List<CartItemDTO> items) {
        log.debug("Reserving stock for {} cart items", items.size());
        List<String> reservationIds = new ArrayList<>();
        try {
            for (CartItemDTO itemDTO : items) {
                InventoryReservationRequest request = new InventoryReservationRequest();
                request.setProductId(itemDTO.getProductId());
                request.setQuantity(itemDTO.getQuantity());
                request.setReservationId(UUID.randomUUID().toString());
                reservationIds.add(request.getReservationId());

                ApiResponse<InventoryReservationResponse> response = productServiceClient.reserveInventory(request).getBody();
                if (response == null || !response.getData().isReserved()) {

                    throw new IllegalArgumentException("Failed to reserve inventory for productId: " + itemDTO.getProductId());
                }
                log.debug("Reserved stock for {} cart item", itemDTO.getProductId());
            }
        } catch (Exception e) {
            rollbackReservations(reservationIds);
            throw new IllegalArgumentException("Failed to reserve inventory" + e.getMessage());
        }
        return reservationIds;
    }

    private void rollbackReservations(List<String> reservationIds) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            return;
        }

        log.warn("Releasing {} reservations", reservationIds.size());
        try {
            log.debug("Releasing back product stock: {}", reservationIds);
            productServiceClient.releaseInventory(reservationIds);
            log.debug("Released back product stock: {}", reservationIds);
        } catch (Exception e) {
            log.error("Failed to release: {}: {}", reservationIds, e.getMessage());
        }
    }

    private List<Order> findMyOrders(Long userId, String sessionId) {
        List<Order> orders = new ArrayList<>();
        if (userId != null) {
            orders = orderRepository.findByUserId(userId);
            return orders;
        } else {
            orders = orderRepository.findBySessionId(sessionId);
            return orders;
        }
    }

    private List<OrderItem> toOrderItem(List<CartItemDTO> dtos, Order order) {
        return dtos.stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(dto.getUnitPrice());
            return item;
        }).toList();
    }

    private OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setUserId(order.getUserId());
        orderDTO.setSessionId(order.getSessionId());
        orderDTO.setShippingAddress(order.getShippingAddress());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setOrderItems(order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProductId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            return itemDTO;
        }).toList());
        orderDTO.setCreatedAt(order.getCreatedAt());
        return orderDTO;
    }
}
