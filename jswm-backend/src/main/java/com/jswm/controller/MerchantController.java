package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
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
}
