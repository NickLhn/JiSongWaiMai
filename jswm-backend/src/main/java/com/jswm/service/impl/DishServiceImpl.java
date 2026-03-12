package com.jswm.service.impl;

import com.jswm.entity.BizDish;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizDishMapper;
import com.jswm.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private BizDishMapper dishMapper;

    @Override
    public BizDish getDishById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public List<BizDish> getDishList(Long merchantId, Long categoryId, String keyword, Integer status) {
        return dishMapper.selectList(merchantId, categoryId, keyword, status);
    }

    @Override
    public void addDish(BizDish dish) {
        dish.setSales(0);
        dish.setRating(new java.math.BigDecimal("5.0"));
        dish.setStatus(0);
        dishMapper.insert(dish);
    }

    @Override
    public void updateDish(Long id, BizDish dish) {
        BizDish existDish = dishMapper.selectById(id);
        if (existDish == null) {
            throw new BusinessException(2001, "菜品不存在");
        }
        dish.setId(id);
        dishMapper.updateById(dish);
    }

    @Override
    public void deleteDish(Long id) {
        BizDish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(2001, "菜品不存在");
        }
        dishMapper.deleteById(id);
    }
}
