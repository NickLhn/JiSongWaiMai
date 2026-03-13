package com.jswm.mapper;

import com.jswm.dto.CartItemDTO;
import com.jswm.entity.BizCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BizCartMapper {
    BizCart selectById(Long id);
    List<BizCart> selectByUserId(Long userId);

    /**
     * 查询购物车详情（包含菜品和商家信息）
     */
    List<CartItemDTO> selectCartDetailByUserId(Long userId);

    BizCart selectByUserAndDish(@Param("userId") Long userId, @Param("dishId") Long dishId);
    int insert(BizCart cart);
    int updateById(BizCart cart);
    int deleteById(Long id);
    int deleteByUserId(Long userId);
}
