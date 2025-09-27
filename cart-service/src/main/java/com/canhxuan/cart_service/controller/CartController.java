package com.canhxuan.cart_service.controller;

import com.canhxuan.cart_service.dto.AddToCartRequest;
import com.canhxuan.cart_service.dto.CartDTO;
import com.canhxuan.cart_service.dto.UpdateCartItemRequest;
import com.canhxuan.cart_service.service.CartService;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/canhxuan/cart")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.getCart(userId, sessionId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody AddToCartRequest addToCartRequest) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.addToCart(userId, sessionId, addToCartRequest));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(HttpServletRequest request, @PathVariable Long itemId, @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.updateCartItem(sessionId, itemId, updateCartItemRequest));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(HttpServletRequest request, @PathVariable Long itemId) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.removeFromCart(sessionId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDTO>> clearCart(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.clearCart(userId, sessionId));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<CartDTO>> mergeCarts(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        String sessionId = request.getSession().getId();
        return ResponseEntity.ok(cartService.mergeCarts(userId, sessionId));
    }
}
