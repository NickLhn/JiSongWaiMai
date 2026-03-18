package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.entity.BizAddress;
import com.jswm.entity.BizCoupon;
import com.jswm.entity.SysUser;
import com.jswm.entity.UserSetting;
import com.jswm.common.Result;
import com.jswm.service.UserCenterService;
import com.jswm.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private UserSettingService userSettingService;

    private Long getUserId() {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        return AuthContext.getUserId();
    }

    // ==================== 收藏相关 ====================
    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> getFavorites() {
        Long userId = getUserId();
        return Result.success(userCenterService.getFavorites(userId));
    }

    @PostMapping("/favorites")
    public Result<Boolean> addFavorite(@RequestBody Map<String, Long> params) {
        Long userId = getUserId();
        Long merchantId = params.get("merchantId");
        return Result.success(userCenterService.addFavorite(userId, merchantId));
    }

    @DeleteMapping("/favorites/{merchantId}")
    public Result<Boolean> removeFavorite(@PathVariable Long merchantId) {
        Long userId = getUserId();
        return Result.success(userCenterService.removeFavorite(userId, merchantId));
    }

    @GetMapping("/favorites/check")
    public Result<Boolean> checkFavorite(@RequestParam Long merchantId) {
        Long userId = getUserId();
        return Result.success(userCenterService.isFavorite(userId, merchantId));
    }

    // ==================== 优惠券相关 ====================
    @GetMapping("/coupons")
    public Result<List<BizCoupon>> getCoupons(@RequestParam(required = false) Integer status) {
        Long userId = getUserId();
        return Result.success(userCenterService.getCoupons(userId, status));
    }

    @GetMapping("/coupons/count")
    public Result<Integer> getAvailableCouponCount() {
        Long userId = getUserId();
        return Result.success(userCenterService.getAvailableCouponCount(userId));
    }

    // ==================== 地址相关 ====================
    @GetMapping("/addresses")
    public Result<List<BizAddress>> getAddresses() {
        Long userId = getUserId();
        return Result.success(userCenterService.getAddresses(userId));
    }

    @GetMapping("/addresses/{id}")
    public Result<BizAddress> getAddressById(@PathVariable Long id) {
        Long userId = getUserId();
        return Result.success(userCenterService.getAddressById(id, userId));
    }

    @GetMapping("/addresses/default")
    public Result<BizAddress> getDefaultAddress() {
        Long userId = getUserId();
        return Result.success(userCenterService.getDefaultAddress(userId));
    }

    @PostMapping("/addresses")
    public Result<Boolean> addAddress(@RequestBody BizAddress address) {
        Long userId = getUserId();
        address.setUserId(userId);
        return Result.success(userCenterService.addAddress(address));
    }

    @PutMapping("/addresses/{id}")
    public Result<Boolean> updateAddress(@PathVariable Long id, @RequestBody BizAddress address) {
        Long userId = getUserId();
        address.setId(id);
        address.setUserId(userId);
        return Result.success(userCenterService.updateAddress(address));
    }

    @DeleteMapping("/addresses/{id}")
    public Result<Boolean> deleteAddress(@PathVariable Long id) {
        Long userId = getUserId();
        return Result.success(userCenterService.deleteAddress(id, userId));
    }

    @PutMapping("/addresses/{id}/default")
    public Result<Boolean> setDefaultAddress(@PathVariable Long id) {
        Long userId = getUserId();
        return Result.success(userCenterService.setDefaultAddress(id, userId));
    }

    // ==================== 用户资料相关 ====================
    @GetMapping("/info")
    public Result<SysUser> getUserInfo() {
        Long userId = getUserId();
        return Result.success(userCenterService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody SysUser user) {
        Long userId = getUserId();
        return Result.success(userCenterService.updateUserInfo(userId, user));
    }

    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        return Result.success(userCenterService.updatePassword(userId, oldPassword, newPassword));
    }

    @GetMapping("/setting")
    public Result<UserSetting> getUserSetting() {
        Long userId = getUserId();
        UserSetting setting = userSettingService.getByUserId(userId);
        if (setting == null) {
            return Result.success(new UserSetting());
        }
        return Result.success(setting);
    }

    @PutMapping("/setting")
    public Result<Boolean> updateUserSetting(@RequestBody UserSetting setting) {
        Long userId = getUserId();
        setting.setUserId(userId);
        return Result.success(userSettingService.saveOrUpdate(setting));
    }

    @PutMapping("/avatar")
    public Result<Boolean> updateAvatar(@RequestBody Map<String, String> params) {
        Long userId = getUserId();
        String avatar = params.get("avatar");
        return Result.success(userCenterService.updateAvatar(userId, avatar));
    }
}
