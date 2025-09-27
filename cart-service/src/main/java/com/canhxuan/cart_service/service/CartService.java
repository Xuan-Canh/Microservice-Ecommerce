package com.canhxuan.cart_service.service;

import com.canhxuan.cart_service.dto.AddToCartRequest;
import com.canhxuan.cart_service.dto.CartDTO;
import com.canhxuan.cart_service.dto.UpdateCartItemRequest;
import com.canhxuan.cart_service.entity.Cart;
import dto.ApiResponse;

public interface CartService {
    ApiResponse<CartDTO> getCart(Long userId, String sessionId);
    ApiResponse<CartDTO> addToCart(Long userId, String sessionId, AddToCartRequest request);
    ApiResponse<CartDTO> updateCartItem(String sessionId, Long itemId, UpdateCartItemRequest request);
    ApiResponse<CartDTO> removeFromCart(String sessionId, Long itemId);
    ApiResponse<CartDTO> clearCart(Long userId, String sessionId);
    ApiResponse<CartDTO> mergeCarts(Long userId, String sessionId);
    ApiResponse<CartDTO> getTotalAmount(Long userId);
}
