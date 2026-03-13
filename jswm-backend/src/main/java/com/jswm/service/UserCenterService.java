package com.jswm.service;

import com.jswm.entity.BizAddress;
import com.jswm.entity.BizCoupon;
import com.jswm.entity.BizFavorite;
import com.jswm.entity.SysUser;
import java.util.List;
import java.util.Map;

public interface UserCenterService {
    
    // 收藏相关
    List<Map<String, Object>> getFavorites(Long userId);
    boolean addFavorite(Long userId, Long merchantId);
    boolean removeFavorite(Long userId, Long merchantId);
    boolean isFavorite(Long userId, Long merchantId);
    
    // 优惠券相关
    List<BizCoupon> getCoupons(Long userId, Integer status);
    int getAvailableCouponCount(Long userId);
    
    // 地址相关
    List<BizAddress> getAddresses(Long userId);
    BizAddress getAddressById(Long id, Long userId);
    BizAddress getDefaultAddress(Long userId);
    boolean addAddress(BizAddress address);
    boolean updateAddress(BizAddress address);
    boolean deleteAddress(Long id, Long userId);
    boolean setDefaultAddress(Long id, Long userId);
    
    // 用户资料相关
    SysUser getUserInfo(Long userId);
    boolean updateUserInfo(Long userId, SysUser user);
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    boolean updateAvatar(Long userId, String avatar);
}
