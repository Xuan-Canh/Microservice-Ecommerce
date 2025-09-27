package com.canhxuan.cart_service.repository;

import com.canhxuan.cart_service.entity.Cart;
import com.canhxuan.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
    void deleteAllByCartId(Long cartId);
}
