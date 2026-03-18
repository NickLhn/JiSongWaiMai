package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.dto.CartItemDTO;
import com.jswm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<CartItemDTO>> getCart() {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        Long userId = AuthContext.getUserId();
        List<CartItemDTO> list = cartService.getCartDetailByUserId(userId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> addToCart(@RequestParam Long dishId,
                                   @RequestParam Long merchantId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        Long userId = AuthContext.getUserId();
        cartService.addToCart(userId, dishId, merchantId, quantity);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCartItem(@PathVariable Long id, @RequestParam Integer quantity) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        cartService.updateCartItem(AuthContext.getUserId(), id, quantity);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCartItem(@PathVariable Long id) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        cartService.deleteCartItem(AuthContext.getUserId(), id);
        return Result.success("删除成功", null);
    }

    @DeleteMapping
    public Result<Void> clearCart() {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        cartService.clearCart(AuthContext.getUserId());
        return Result.success("清空成功", null);
    }
}
