package com.jswm.service;

import com.jswm.entity.BizMerchant;
import java.util.List;

public interface MerchantService {
    BizMerchant getMerchantById(Long id);
    BizMerchant getMerchantByUserId(Long userId);
    List<BizMerchant> getMerchantList(Integer status, String keyword);
    void addMerchant(BizMerchant merchant);
    void updateMerchant(Long id, BizMerchant merchant);
    void deleteMerchant(Long id);
}
