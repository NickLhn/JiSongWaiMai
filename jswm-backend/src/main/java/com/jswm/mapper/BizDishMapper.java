package com.jswm.mapper;

import com.jswm.entity.BizDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BizDishMapper {
    BizDish selectById(Long id);
    List<BizDish> selectList(@Param("merchantId") Long merchantId, @Param("categoryId") Long categoryId,
                            @Param("keyword") String keyword, @Param("status") Integer status);
    int insert(BizDish dish);
    int updateById(BizDish dish);
    int deleteById(Long id);
}
