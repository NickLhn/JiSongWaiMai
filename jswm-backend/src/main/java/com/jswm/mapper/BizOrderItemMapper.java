package com.jswm.mapper;

import com.jswm.entity.BizOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BizOrderItemMapper {
    BizOrderItem selectById(Long id);
    List<BizOrderItem> selectByOrderId(Long orderId);
    int insert(BizOrderItem orderItem);
    int insertBatch(@Param("items") List<BizOrderItem> items);
    int deleteByOrderId(Long orderId);
}
