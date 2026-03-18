package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.entity.BizDish;
import com.jswm.entity.BizMerchant;
import com.jswm.exception.BusinessException;
import com.jswm.service.DishService;
import com.jswm.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/{id}")
    public Result<BizDish> getDishById(@PathVariable Long id) {
        BizDish dish = dishService.getDishById(id);
        return Result.success(dish);
    }

    @GetMapping
    public Result<List<BizDish>> getDishList(@RequestParam(required = false) Long merchantId,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer status) {
        List<BizDish> list = dishService.getDishList(merchantId, categoryId, keyword, status);
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> addDish(@RequestBody BizDish dish) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        if (Constants.USER_ROLE_MERCHANT.equals(AuthContext.getRole())) {
            BizMerchant merchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
            if (merchant == null) {
                return Result.error("未找到商家信息");
            }
            dish.setMerchantId(merchant.getId());
        } else if (dish.getMerchantId() == null) {
            throw new BusinessException(400, "商家ID不能为空");
        }
        dishService.addDish(dish);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateDish(@PathVariable Long id, @RequestBody BizDish dish) {
        validateDishOwnership(id);
        dishService.updateDish(id, dish);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        validateDishOwnership(id);
        dishService.deleteDish(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取当前商家的菜品列表
     */
    @GetMapping("/my")
    public Result<List<BizDish>> getMyDishes(@RequestParam(required = false) Integer status) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        BizMerchant merchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
        if (merchant == null) {
            return Result.error("未找到商家信息");
        }
        List<BizDish> list = dishService.getDishList(merchant.getId(), null, null, status);
        return Result.success(list);
    }

    private void validateDishOwnership(Long dishId) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        if (Constants.USER_ROLE_ADMIN.equals(AuthContext.getRole())) {
            return;
        }
        BizDish existingDish = dishService.getDishById(dishId);
        if (existingDish == null) {
            throw new BusinessException(2001, "菜品不存在");
        }
        BizMerchant merchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
        if (merchant == null || !merchant.getId().equals(existingDish.getMerchantId())) {
            throw new BusinessException(403, "无权操作该菜品");
        }
    }
}
