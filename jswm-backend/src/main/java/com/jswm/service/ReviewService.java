package com.jswm.service;

import com.jswm.entity.BizReview;

import java.util.List;
import java.util.Map;

/**
 * 评价服务接口
 */
public interface ReviewService {

    /**
     * 根据订单ID获取评价
     */
    BizReview getReviewByOrderId(Long orderId);

    /**
     * 获取商家的评价列表
     */
    List<BizReview> getMerchantReviews(Long merchantId, Integer rating, Integer page, Integer pageSize);

    /**
     * 获取商家的评价统计
     */
    Map<String, Object> getMerchantReviewStats(Long merchantId);

    /**
     * 获取用户的评价列表
     */
    List<BizReview> getUserReviews(Long userId, Integer page, Integer pageSize);

    /**
     * 提交评价
     */
    void submitReview(Long userId, BizReview review);

    /**
     * 删除评价
     */
    void deleteReview(Long id, Long userId, Integer role);
}
