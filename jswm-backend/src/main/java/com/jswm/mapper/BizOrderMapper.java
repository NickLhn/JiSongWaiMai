package com.jswm.mapper;

import com.jswm.entity.BizOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BizOrderMapper {
    BizOrder selectById(Long id);
    BizOrder selectByOrderNo(String orderNo);
    List<BizOrder> selectList(@Param("userId") Long userId, @Param("merchantId") Long merchantId,
                               @Param("status") Integer status);
    int insert(BizOrder order);
    int updateById(BizOrder order);
    int deleteById(Long id);
}
