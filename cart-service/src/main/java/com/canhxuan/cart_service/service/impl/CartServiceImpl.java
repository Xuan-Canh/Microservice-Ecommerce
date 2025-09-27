package com.canhxuan.cart_service.service.impl;

import com.canhxuan.cart_service.client.ProductServiceClient;
import com.canhxuan.cart_service.dto.*;
import com.canhxuan.cart_service.entity.Cart;
import com.canhxuan.cart_service.entity.CartItem;
import com.canhxuan.cart_service.repository.CartItemRepository;
import com.canhxuan.cart_service.repository.CartRepository;
import com.canhxuan.cart_service.service.CartService;
import dto.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private ProductServiceClient productServiceClient;


    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceClient = productServiceClient;
    }


    @Override
    public ApiResponse<CartDTO> getCart(Long userId, String sessionId) {
        Cart cart = findOrCreateCart(userId, sessionId);
        CartDTO cartDTO = toCartDTO(cart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get cart successfully");
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> addToCart(Long userId, String sessionId, AddToCartRequest request) {
        validateAddToCartRequest(request);
        ProductDTO product = getProductById(request.getProductId());
        Cart cart = findOrCreateCart(userId, sessionId);
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductId(cart, request.getProductId());
        if (existingItemOpt.isPresent()) {
            if (product.getQuantity() < request.getQuantity() + existingItemOpt.get().getQuantity() ) {
                throw new RuntimeException("Product is out of stock" + product.getName());
            }
            CartItem cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            if (product.getQuantity() < request.getQuantity()) {
                throw new RuntimeException("Product is out of stock" + product.getName());
            }
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setProductName(product.getName());
            newItem.setProductImage(product.getImageUrl());
            newItem.setUnitPrice(product.getPrice());
            cartItemRepository.save(newItem);
            cart.getCartItems().add(newItem);
        }
        recalculateCartTotals(cart);
        cart = cartRepository.save(cart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(201);
        response.setMessage("Add to cart successfully");
        CartDTO cartDTO = toCartDTO(cart);
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> updateCartItem(String sessionId, Long itemId, UpdateCartItemRequest request) {
        ProductDTO product = getProductById(itemId);
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found with session id: " + sessionId));
        CartItem existingItem = cartItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));
        if (!existingItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the session");
        }
        if (product.getQuantity() < request.getQuantity() + existingItem.getQuantity()) {
            throw new RuntimeException("Product is out of stock" + existingItem.getProductName());
        }
        existingItem.setQuantity(request.getQuantity());
        cartItemRepository.save(existingItem);
        recalculateCartTotals(cart);
        cart = cartRepository.save(cart);
        CartDTO cartDTO = toCartDTO(cart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Update cart item successfully");
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> removeFromCart(String sessionId, Long itemId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem existingItem = cartItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));
        if (!existingItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the session");
        }
        cartItemRepository.delete(existingItem);
        cart.getCartItems().remove(existingItem);
        recalculateCartTotals(cart);
        cart = cartRepository.save(cart);
        CartDTO cartDTO = toCartDTO(cart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Remove from cart successfully");
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> clearCart(Long userId, String sessionId) {
        Cart cart = findOrCreateCart(userId, sessionId);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getCartItems().clear();
        recalculateCartTotals(cart);
        cart = cartRepository.save(cart);
        CartDTO cartDTO = toCartDTO(cart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Clear cart successfully");
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> mergeCarts(Long userId, String sessionId) {
        Optional<Cart> userCart = cartRepository.findByUserId(userId);
        Optional<Cart> sessionCart = cartRepository.findBySessionId(sessionId);
        if (sessionCart.isEmpty()) {
            if (userCart.isPresent()) {
                Cart cart = userCart.get();
                CartDTO cartDTO = toCartDTO(cart);
                ApiResponse<CartDTO> response = new ApiResponse<>();
                response.setStatus(200);
                response.setMessage("Merge carts successfully");
                response.setData(cartDTO);
                return response;
            }
            Cart cart = findOrCreateCart(userId, sessionId);
            CartDTO cartDTO = toCartDTO(cart);
            ApiResponse<CartDTO> response = new ApiResponse<>();
            response.setStatus(200);
            response.setMessage("Merge carts successfully");
            response.setData(cartDTO);
            return response;
        }

        if (userCart.isEmpty()) {
            Cart cart = sessionCart.get();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
            CartDTO cartDTO = toCartDTO(cart);
            ApiResponse<CartDTO> response = new ApiResponse<>();
            response.setStatus(200);
            response.setMessage("Merge carts successfully");
            response.setData(cartDTO);
            return response;
        }

        Cart targetCart = userCart.get();
        Cart sourceCart = sessionCart.get();
        for (CartItem sourceItem : sourceCart.getCartItems()) {
            Optional<CartItem> existingItem = targetCart.getCartItems().stream()
                    .filter(item -> item.getProductId().equals(sourceItem.getProductId()))
                    .findFirst();
            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(sourceItem.getQuantity() + existingItem.get().getQuantity());
            } else {
                sourceItem.setCart(targetCart);
                targetCart.getCartItems().add(sourceItem);
            }
        }
        cartRepository.delete(sourceCart);
        recalculateCartTotals(targetCart);
        targetCart = cartRepository.save(targetCart);
        CartDTO cartDTO = toCartDTO(targetCart);
        ApiResponse<CartDTO> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Merge carts successfully");
        response.setData(cartDTO);
        return response;
    }

    @Override
    public ApiResponse<CartDTO> getTotalAmount(Long userId) {
        return null;
    }


    private Cart findOrCreateCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId).orElseGet(() -> createNewCart(userId, sessionId));
        }

        return cartRepository.findBySessionId(sessionId).orElseGet(() -> createNewCart(userId, sessionId));
    }

    private Cart createNewCart(Long userId, String sessionId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setSessionId(sessionId);
        cart.setCreatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private List<CartItemDTO> toCartItemDTOS(List<CartItem> cartItems) {
        return cartItems.stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            dto.setTotalPrice(item.getTotalPrice());
            dto.setProductImage(item.getProductImage());
            dto.setProductName(item.getProductName());
            dto.setCreatedAt(item.getCreatedAt());
            return dto;
        }).toList();
    }

    private CartDTO toCartDTO(Cart cart) {
        if (cart == null) return null;
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUserId());
        cartDTO.setSessionId(cart.getSessionId());
        cartDTO.setItems(toCartItemDTOS(cart.getCartItems()));
        cartDTO.setTotalAmount(cart.getTotalAmount());
        cartDTO.setTotalQuantity(cart.getTotalQuantity());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());
        return cartDTO;
    }

    private void recalculateCartTotals(Cart cart) {
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cart.setUpdatedAt(LocalDateTime.now());
    }

    private ProductDTO getProductById(Long productId) {
        try {
            ResponseEntity<ApiResponse<ProductDTO>> response = productServiceClient.getProduct(productId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ProductDTO product = response.getBody().getData();
                return product;
            }
            throw new RuntimeException("Product not found");
        } catch (Exception e) {
            throw new RuntimeException("Product service is unavailable");
        }
    }

    private void validateAddToCartRequest(AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (request.getQuantity() > 999) {
            throw new IllegalArgumentException("Quantity cannot exceed 999");
        }
    }
}
