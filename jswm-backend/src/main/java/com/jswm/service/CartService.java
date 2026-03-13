package com.jswm.service;

import com.jswm.dto.CartItemDTO;
import com.jswm.entity.BizCart;
import java.util.List;

public interface CartService {
    List<BizCart> getCartByUserId(Long userId);

    /**
     * 获取购物车详情（包含菜品和商家信息）
     */
    List<CartItemDTO> getCartDetailByUserId(Long userId);

    void addToCart(Long userId, Long dishId, Long merchantId, Integer quantity);
    void updateCartItem(Long id, Integer quantity);
    void deleteCartItem(Long id);
    void clearCart(Long userId);
}
