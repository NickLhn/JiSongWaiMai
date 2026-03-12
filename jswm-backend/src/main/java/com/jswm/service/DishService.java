package com.jswm.service;

import com.jswm.entity.BizDish;
import java.util.List;

public interface DishService {
    BizDish getDishById(Long id);
    List<BizDish> getDishList(Long merchantId, Long categoryId, String keyword, Integer status);
    void addDish(BizDish dish);
    void updateDish(Long id, BizDish dish);
    void deleteDish(Long id);
}
