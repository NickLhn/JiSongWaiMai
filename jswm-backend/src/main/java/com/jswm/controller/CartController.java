package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.dto.CartItemDTO;
import com.jswm.entity.BizCart;
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
    public Result<List<CartItemDTO>> getCart(@RequestHeader("Authorization") String token) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        List<CartItemDTO> list = cartService.getCartDetailByUserId(userId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> addToCart(@RequestHeader("Authorization") String token,
                                   @RequestParam Long dishId,
                                   @RequestParam Long merchantId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        cartService.addToCart(userId, dishId, merchantId, quantity);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCartItem(@PathVariable Long id, @RequestParam Integer quantity) {
        cartService.updateCartItem(id, quantity);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return Result.success("删除成功", null);
    }

    @DeleteMapping
    public Result<Void> clearCart(@RequestHeader("Authorization") String token) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        cartService.clearCart(userId);
        return Result.success("清空成功", null);
    }
}
