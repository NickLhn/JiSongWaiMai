package com.jswm.mapper;

import com.jswm.entity.BizAddress;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BizAddressMapper {
    
    @Insert("INSERT INTO biz_address (user_id, receiver_name, receiver_phone, province, city, district, " +
            "detail_address, label, is_default, create_time, update_time, is_deleted) " +
            "VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, " +
            "#{detailAddress}, #{label}, #{isDefault}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizAddress address);
    
    @Select("SELECT * FROM biz_address WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY is_default DESC, create_time DESC")
    List<BizAddress> selectByUserId(Long userId);
    
    @Select("SELECT * FROM biz_address WHERE id = #{id} AND user_id = #{userId} AND is_deleted = 0")
    BizAddress selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Select("SELECT * FROM biz_address WHERE user_id = #{userId} AND is_default = 1 AND is_deleted = 0 LIMIT 1")
    BizAddress selectDefaultByUserId(Long userId);
    
    @Update("UPDATE biz_address SET receiver_name = #{receiverName}, receiver_phone = #{receiverPhone}, " +
            "province = #{province}, city = #{city}, district = #{district}, " +
            "detail_address = #{detailAddress}, label = #{label}, is_default = #{isDefault}, " +
            "update_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int update(BizAddress address);
    
    @Update("UPDATE biz_address SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultByUserId(Long userId);
    
    @Update("UPDATE biz_address SET is_default = 1 WHERE id = #{id} AND user_id = #{userId}")
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
    
    @Update("UPDATE biz_address SET is_deleted = 1, update_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
