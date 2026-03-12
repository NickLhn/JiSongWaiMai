package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizDish;
import com.jswm.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

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
        dishService.addDish(dish);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateDish(@PathVariable Long id, @RequestBody BizDish dish) {
        dishService.updateDish(id, dish);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success("删除成功", null);
    }
}
