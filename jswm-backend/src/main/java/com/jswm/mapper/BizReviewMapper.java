package com.jswm.mapper;

import com.jswm.entity.BizReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface BizReviewMapper {

    BizReview selectById(Long id);

    BizReview selectByOrderId(Long orderId);

    List<BizReview> selectByMerchantId(@Param("merchantId") Long merchantId,
                                        @Param("rating") Integer rating,
                                        @Param("offset") Integer offset,
                                        @Param("pageSize") Integer pageSize);

    List<BizReview> selectByUserId(@Param("userId") Long userId,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    int insert(BizReview review);
    int updateById(BizReview review);

    int deleteById(Long id);

    int countByMerchantId(Long merchantId);

    int countByRating(@Param("merchantId") Long merchantId,
                      @Param("rating") Integer rating);

    BigDecimal selectAverageRating(Long merchantId);
}
