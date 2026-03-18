package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.exception.BusinessException;
import com.jswm.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/{id}")
    public Result<BizMerchant> getMerchantById(@PathVariable Long id) {
        BizMerchant merchant = merchantService.getMerchantById(id);
        return Result.success(merchant);
    }

    @GetMapping
    public Result<List<BizMerchant>> getMerchantList(@RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String keyword) {
        List<BizMerchant> list = merchantService.getMerchantList(status, keyword);
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> addMerchant(@RequestBody BizMerchant merchant) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        if (!Constants.USER_ROLE_ADMIN.equals(AuthContext.getRole())) {
            merchant.setUserId(AuthContext.getUserId());
        } else if (merchant.getUserId() == null) {
            throw new BusinessException(400, "商家用户ID不能为空");
        }
        merchantService.addMerchant(merchant);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateMerchant(@PathVariable Long id, @RequestBody BizMerchant merchant) {
        validateMerchantOwnership(id);
        merchantService.updateMerchant(id, merchant);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMerchant(@PathVariable Long id) {
        AuthContext.requireRole(Constants.USER_ROLE_ADMIN);
        merchantService.deleteMerchant(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取当前登录商家的店铺信息
     */
    @GetMapping("/my")
    public Result<BizMerchant> getMyMerchantInfo() {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        Long userId = AuthContext.getUserId();
        // 根据用户ID查询商家信息
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return Result.error("未找到店铺信息");
        }
        return Result.success(merchant);
    }

    /**
     * 更新当前登录商家的店铺信息
     */
    @PutMapping("/my")
    public Result<Void> updateMyMerchantInfo(@RequestBody BizMerchant merchant) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        Long userId = AuthContext.getUserId();
        BizMerchant existingMerchant = merchantService.getMerchantByUserId(userId);
        if (existingMerchant == null) {
            return Result.error("未找到店铺信息");
        }
        merchantService.updateMerchant(existingMerchant.getId(), merchant);
        return Result.success("更新成功", null);
    }

    private void validateMerchantOwnership(Long merchantId) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        if (Constants.USER_ROLE_ADMIN.equals(AuthContext.getRole())) {
            return;
        }
        BizMerchant existingMerchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
        if (existingMerchant == null || !existingMerchant.getId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该店铺");
        }
    }
}
