package com.jswm.service.impl;

import com.jswm.entity.*;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.*;
import com.jswm.service.UserCenterService;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserCenterServiceImpl implements UserCenterService {

    @Autowired
    private BizFavoriteMapper favoriteMapper;
    
    @Autowired
    private BizCouponMapper couponMapper;
    
    @Autowired
    private BizAddressMapper addressMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private BizMerchantMapper merchantMapper;

    @Override
    public List<Map<String, Object>> getFavorites(Long userId) {
        List<BizFavorite> favorites = favoriteMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (BizFavorite favorite : favorites) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", favorite.getId());
            item.put("createTime", favorite.getCreateTime());
            
            BizMerchant merchant = merchantMapper.selectById(favorite.getMerchantId());
            if (merchant != null) {
                Map<String, Object> merchantInfo = new HashMap<>();
                merchantInfo.put("id", merchant.getId());
                merchantInfo.put("shopName", merchant.getShopName());
                merchantInfo.put("shopLogo", merchant.getShopLogo());
                merchantInfo.put("shopAddress", merchant.getShopAddress());
                merchantInfo.put("rating", merchant.getRating());
                merchantInfo.put("sales", merchant.getSales());
                merchantInfo.put("category", merchant.getCategory());
                item.put("merchant", merchantInfo);
            }
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean addFavorite(Long userId, Long merchantId) {
        if (isFavorite(userId, merchantId)) {
            throw new BusinessException(2001, "已收藏该商家");
        }
        BizFavorite favorite = new BizFavorite();
        favorite.setUserId(userId);
        favorite.setMerchantId(merchantId);
        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    public boolean removeFavorite(Long userId, Long merchantId) {
        return favoriteMapper.deleteByUserAndMerchant(userId, merchantId) > 0;
    }

    @Override
    public boolean isFavorite(Long userId, Long merchantId) {
        return favoriteMapper.countByUserAndMerchant(userId, merchantId) > 0;
    }

    @Override
    public List<BizCoupon> getCoupons(Long userId, Integer status) {
        if (status != null) {
            return couponMapper.selectByUserIdAndStatus(userId, status);
        }
        return couponMapper.selectByUserId(userId);
    }

    @Override
    public int getAvailableCouponCount(Long userId) {
        return couponMapper.countAvailableByUserId(userId);
    }

    @Override
    public List<BizAddress> getAddresses(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public BizAddress getAddressById(Long id, Long userId) {
        return addressMapper.selectByIdAndUserId(id, userId);
    }

    @Override
    public BizAddress getDefaultAddress(Long userId) {
        return addressMapper.selectDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public boolean addAddress(BizAddress address) {
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(address.getUserId());
        }
        return addressMapper.insert(address) > 0;
    }

    @Override
    @Transactional
    public boolean updateAddress(BizAddress address) {
        BizAddress exist = addressMapper.selectByIdAndUserId(address.getId(), address.getUserId());
        if (exist == null) {
            throw new BusinessException(2002, "地址不存在");
        }
        
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(address.getUserId());
        }
        return addressMapper.update(address) > 0;
    }

    @Override
    public boolean deleteAddress(Long id, Long userId) {
        return addressMapper.deleteById(id, userId) > 0;
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long id, Long userId) {
        addressMapper.clearDefaultByUserId(userId);
        return addressMapper.setDefault(id, userId) > 0;
    }

    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public boolean updateUserInfo(Long userId, SysUser user) {
        SysUser exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException(2003, "用户不存在");
        }
        
        // 只允许修改指定字段
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setRealName(user.getRealName());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        
        return userMapper.updateById(updateUser) > 0;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(2003, "用户不存在");
        }
        
        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException(2004, "原密码错误");
        }
        
        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        
        return userMapper.updateById(updateUser) > 0;
    }

    @Override
    public boolean updateAvatar(Long userId, String avatar) {
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setAvatar(avatar);
        return userMapper.updateById(updateUser) > 0;
    }
}
