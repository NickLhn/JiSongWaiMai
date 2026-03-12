package com.jswm.mapper;

import com.jswm.entity.BizMerchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BizMerchantMapper {
    BizMerchant selectById(Long id);
    BizMerchant selectByUserId(Long userId);
    List<BizMerchant> selectList(@Param("status") Integer status, @Param("keyword") String keyword);
    int insert(BizMerchant merchant);
    int updateById(BizMerchant merchant);
    int deleteById(Long id);
}
