package com.jswm.mapper;

import com.jswm.entity.BizCoupon;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BizCouponMapper {
    
    @Insert("INSERT INTO biz_coupon (user_id, name, type, min_amount, discount_amount, discount_rate, " +
            "merchant_id, start_time, end_time, status) " +
            "VALUES (#{userId}, #{name}, #{type}, #{minAmount}, #{discountAmount}, #{discountRate}, " +
            "#{merchantId}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizCoupon coupon);
    
    @Select("SELECT * FROM biz_coupon WHERE user_id = #{userId} AND status = #{status} AND end_time > NOW() " +
            "ORDER BY end_time ASC")
    List<BizCoupon> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    @Select("SELECT * FROM biz_coupon WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<BizCoupon> selectByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM biz_coupon WHERE user_id = #{userId} AND status = 0 AND end_time > NOW()")
    int countAvailableByUserId(Long userId);
    
    @Update("UPDATE biz_coupon SET status = #{status}, use_time = #{useTime}, order_id = #{orderId} WHERE id = #{id}")
    int updateStatus(BizCoupon coupon);
    
    @Delete("DELETE FROM biz_coupon WHERE id = #{id} AND user_id = #{userId}")
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
