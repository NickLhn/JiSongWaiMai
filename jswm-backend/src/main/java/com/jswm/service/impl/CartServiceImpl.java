package com.jswm.service.impl;

import com.jswm.dto.CartItemDTO;
import com.jswm.entity.BizCart;
import com.jswm.entity.BizDish;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizCartMapper;
import com.jswm.mapper.BizDishMapper;
import com.jswm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private BizCartMapper cartMapper;

    @Autowired
    private BizDishMapper dishMapper;

    @Override
    public List<BizCart> getCartByUserId(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public List<CartItemDTO> getCartDetailByUserId(Long userId) {
        return cartMapper.selectCartDetailByUserId(userId);
    }

    @Override
    public void addToCart(Long userId, Long dishId, Long merchantId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(3006, "商品数量必须大于0");
        }

        BizDish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException(3007, "商品不存在");
        }
        if (!dish.getMerchantId().equals(merchantId)) {
            throw new BusinessException(3008, "商品与商家信息不匹配");
        }

        List<BizCart> cartItems = cartMapper.selectByUserId(userId);
        boolean hasOtherMerchant = cartItems.stream()
                .anyMatch(item -> !item.getMerchantId().equals(merchantId));
        if (hasOtherMerchant) {
            throw new BusinessException(3009, "购物车中已有其他商家商品，请先清空后再添加");
        }

        BizCart existCart = cartMapper.selectByUserAndDish(userId, dishId);
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + quantity);
            cartMapper.updateById(existCart);
        } else {
            BizCart cart = new BizCart();
            cart.setUserId(userId);
            cart.setDishId(dishId);
            cart.setMerchantId(merchantId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateCartItem(Long userId, Long id, Integer quantity) {
        BizCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(3001, "购物车商品不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException(3002, "无权操作该购物车商品");
        }
        if (quantity <= 0) {
            cartMapper.deleteById(id);
        } else {
            cart.setQuantity(quantity);
            cartMapper.updateById(cart);
        }
    }

    @Override
    public void deleteCartItem(Long userId, Long id) {
        BizCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(3001, "购物车商品不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException(3002, "无权操作该购物车商品");
        }
        cartMapper.deleteById(id);
    }

    @Override
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}
