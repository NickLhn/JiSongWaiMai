package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.mapper.BizMerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/merchants")
public class AdminMerchantController {

    @Autowired
    private BizMerchantMapper merchantMapper;

    @GetMapping
    public Result<Map<String, Object>> getMerchantList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取所有商家
        List<BizMerchant> allMerchants = merchantMapper.selectList(status, keyword);

        // 分页
        int total = allMerchants.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<BizMerchant> pageData = start < total ? allMerchants.subList(start, end) : List.of();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BizMerchant> getMerchantDetail(@PathVariable Long id) {
        BizMerchant merchant = merchantMapper.selectById(id);
        return Result.success(merchant);
    }

    @PutMapping("/{id}")
    public Result<Void> updateMerchant(@PathVariable Long id, @RequestBody BizMerchant merchant) {
        merchant.setId(id);
        merchantMapper.updateById(merchant);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateMerchantStatus(@PathVariable Long id, @RequestParam Integer status) {
        BizMerchant merchant = new BizMerchant();
        merchant.setId(id);
        merchant.setStatus(status);
        merchantMapper.updateById(merchant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMerchant(@PathVariable Long id) {
        merchantMapper.deleteById(id);
        return Result.success();
    }
}
