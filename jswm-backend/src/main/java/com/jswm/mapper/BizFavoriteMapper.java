package com.jswm.mapper;

import com.jswm.entity.BizFavorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BizFavoriteMapper {
    
    @Insert("INSERT INTO biz_favorite (user_id, merchant_id, create_time) VALUES (#{userId}, #{merchantId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizFavorite favorite);
    
    @Select("SELECT * FROM biz_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<BizFavorite> selectByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM biz_favorite WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    int countByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);
    
    @Delete("DELETE FROM biz_favorite WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    int deleteByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);
    
    @Delete("DELETE FROM biz_favorite WHERE id = #{id}")
    int deleteById(Long id);
}
