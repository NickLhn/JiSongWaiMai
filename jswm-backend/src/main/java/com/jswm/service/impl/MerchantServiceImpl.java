package com.jswm.service.impl;

import com.jswm.entity.BizMerchant;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizMerchantMapper;
import com.jswm.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private BizMerchantMapper merchantMapper;

    @Override
    public BizMerchant getMerchantById(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public BizMerchant getMerchantByUserId(Long userId) {
        return merchantMapper.selectByUserId(userId);
    }

    @Override
    public List<BizMerchant> getMerchantList(Integer status, String keyword) {
        return merchantMapper.selectList(status, keyword);
    }

    @Override
    public void addMerchant(BizMerchant merchant) {
        BizMerchant existMerchant = merchantMapper.selectByUserId(merchant.getUserId());
        if (existMerchant != null) {
            throw new BusinessException(2001, "该用户已存在商家信息");
        }
        merchant.setRating(new java.math.BigDecimal("5.0"));
        merchant.setSales(0);
        merchant.setStatus(0);
        merchantMapper.insert(merchant);
    }

    @Override
    public void updateMerchant(Long id, BizMerchant merchant) {
        BizMerchant existMerchant = merchantMapper.selectById(id);
        if (existMerchant == null) {
            throw new BusinessException(2002, "商家不存在");
        }
        merchant.setId(id);
        merchantMapper.updateById(merchant);
    }

    @Override
    public void deleteMerchant(Long id) {
        BizMerchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BusinessException(2002, "商家不存在");
        }
        merchantMapper.deleteById(id);
    }
}
