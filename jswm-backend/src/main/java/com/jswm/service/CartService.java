package com.jswm.service;

import com.jswm.entity.BizCart;
import java.util.List;

public interface CartService {
    List<BizCart> getCartByUserId(Long userId);
    void addToCart(Long userId, Long dishId, Long merchantId, Integer quantity);
    void updateCartItem(Long id, Integer quantity);
    void deleteCartItem(Long id);
    void clearCart(Long userId);
}
