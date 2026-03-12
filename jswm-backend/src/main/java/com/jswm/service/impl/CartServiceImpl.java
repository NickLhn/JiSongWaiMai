package com.jswm.service.impl;

import com.jswm.entity.BizCart;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizCartMapper;
import com.jswm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private BizCartMapper cartMapper;

    @Override
    public List<BizCart> getCartByUserId(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public void addToCart(Long userId, Long dishId, Long merchantId, Integer quantity) {
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
    public void updateCartItem(Long id, Integer quantity) {
        BizCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(3001, "购物车商品不存在");
        }
        if (quantity <= 0) {
            cartMapper.deleteById(id);
        } else {
            cart.setQuantity(quantity);
            cartMapper.updateById(cart);
        }
    }

    @Override
    public void deleteCartItem(Long id) {
        BizCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(3001, "购物车商品不存在");
        }
        cartMapper.deleteById(id);
    }

    @Override
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}
