package com.jswm.controller;

import com.jswm.entity.BizAddress;
import com.jswm.entity.BizCoupon;
import com.jswm.entity.SysUser;
import com.jswm.service.UserCenterService;
import com.jswm.utils.JwtUtils;
import com.jswm.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtils.getUserId(token);
    }

    // ==================== 收藏相关 ====================
    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> getFavorites(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getFavorites(userId));
    }

    @PostMapping("/favorites")
    public Result<Boolean> addFavorite(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        Long merchantId = params.get("merchantId");
        return Result.success(userCenterService.addFavorite(userId, merchantId));
    }

    @DeleteMapping("/favorites/{merchantId}")
    public Result<Boolean> removeFavorite(@PathVariable Long merchantId, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.removeFavorite(userId, merchantId));
    }

    @GetMapping("/favorites/check")
    public Result<Boolean> checkFavorite(@RequestParam Long merchantId, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.isFavorite(userId, merchantId));
    }

    // ==================== 优惠券相关 ====================
    @GetMapping("/coupons")
    public Result<List<BizCoupon>> getCoupons(@RequestParam(required = false) Integer status, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getCoupons(userId, status));
    }

    @GetMapping("/coupons/count")
    public Result<Integer> getAvailableCouponCount(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getAvailableCouponCount(userId));
    }

    // ==================== 地址相关 ====================
    @GetMapping("/addresses")
    public Result<List<BizAddress>> getAddresses(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getAddresses(userId));
    }

    @GetMapping("/addresses/{id}")
    public Result<BizAddress> getAddressById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getAddressById(id, userId));
    }

    @GetMapping("/addresses/default")
    public Result<BizAddress> getDefaultAddress(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getDefaultAddress(userId));
    }

    @PostMapping("/addresses")
    public Result<Boolean> addAddress(@RequestBody BizAddress address, HttpServletRequest request) {
        Long userId = getUserId(request);
        address.setUserId(userId);
        return Result.success(userCenterService.addAddress(address));
    }

    @PutMapping("/addresses/{id}")
    public Result<Boolean> updateAddress(@PathVariable Long id, @RequestBody BizAddress address, HttpServletRequest request) {
        Long userId = getUserId(request);
        address.setId(id);
        address.setUserId(userId);
        return Result.success(userCenterService.updateAddress(address));
    }

    @DeleteMapping("/addresses/{id}")
    public Result<Boolean> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.deleteAddress(id, userId));
    }

    @PutMapping("/addresses/{id}/default")
    public Result<Boolean> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.setDefaultAddress(id, userId));
    }

    // ==================== 用户资料相关 ====================
    @GetMapping("/info")
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody SysUser user, HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(userCenterService.updateUserInfo(userId, user));
    }

    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        return Result.success(userCenterService.updatePassword(userId, oldPassword, newPassword));
    }

    @PutMapping("/avatar")
    public Result<Boolean> updateAvatar(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        String avatar = params.get("avatar");
        return Result.success(userCenterService.updateAvatar(userId, avatar));
    }
}
