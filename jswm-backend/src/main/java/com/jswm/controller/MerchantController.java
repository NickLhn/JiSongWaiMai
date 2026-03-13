package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.service.MerchantService;
import com.jswm.utils.JwtUtils;
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
        merchantService.addMerchant(merchant);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateMerchant(@PathVariable Long id, @RequestBody BizMerchant merchant) {
        merchantService.updateMerchant(id, merchant);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取当前登录商家的店铺信息
     */
    @GetMapping("/my")
    public Result<BizMerchant> getMyMerchantInfo(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtils.getUserId(token);
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
    public Result<Void> updateMyMerchantInfo(@RequestHeader("Authorization") String token,
                                              @RequestBody BizMerchant merchant) {
        Long userId = JwtUtils.getUserId(token);
        BizMerchant existingMerchant = merchantService.getMerchantByUserId(userId);
        if (existingMerchant == null) {
            return Result.error("未找到店铺信息");
        }
        merchantService.updateMerchant(existingMerchant.getId(), merchant);
        return Result.success("更新成功", null);
    }
}
