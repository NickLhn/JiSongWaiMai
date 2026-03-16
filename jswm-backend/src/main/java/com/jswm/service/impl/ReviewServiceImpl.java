package com.jswm.service.impl;

import com.jswm.common.Constants;
import com.jswm.entity.BizOrder;
import com.jswm.entity.BizReview;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizOrderMapper;
import com.jswm.mapper.BizReviewMapper;
import com.jswm.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private BizReviewMapper reviewMapper;

    @Autowired
    private BizOrderMapper orderMapper;

    @Override
    public BizReview getReviewByOrderId(Long orderId) {
        return reviewMapper.selectByOrderId(orderId);
    }

    @Override
    public List<BizReview> getMerchantReviews(Long merchantId, Integer rating, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return reviewMapper.selectByMerchantId(merchantId, rating, offset, pageSize);
    }

    @Override
    public Map<String, Object> getMerchantReviewStats(Long merchantId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总评价数
        int totalCount = reviewMapper.countByMerchantId(merchantId);
        stats.put("totalCount", totalCount);
        
        if (totalCount == 0) {
            stats.put("averageRating", 0);
            stats.put("fiveStarCount", 0);
            stats.put("fourStarCount", 0);
            stats.put("threeStarCount", 0);
            stats.put("twoStarCount", 0);
            stats.put("oneStarCount", 0);
            return stats;
        }
        
        // 平均评分
        BigDecimal avgRating = reviewMapper.selectAverageRating(merchantId);
        stats.put("averageRating", avgRating.setScale(1, RoundingMode.HALF_UP));
        
        // 各星级评价数
        stats.put("fiveStarCount", reviewMapper.countByRating(merchantId, 5));
        stats.put("fourStarCount", reviewMapper.countByRating(merchantId, 4));
        stats.put("threeStarCount", reviewMapper.countByRating(merchantId, 3));
        stats.put("twoStarCount", reviewMapper.countByRating(merchantId, 2));
        stats.put("oneStarCount", reviewMapper.countByRating(merchantId, 1));
        
        return stats;
    }

    @Override
    public List<BizReview> getUserReviews(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return reviewMapper.selectByUserId(userId, offset, pageSize);
    }

    @Override
    @Transactional
    public void submitReview(Long userId, BizReview review) {
        // 1. 检查订单是否存在且属于当前用户
        BizOrder order = orderMapper.selectById(review.getOrderId());
        if (order == null) {
            throw new BusinessException(4001, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(4002, "无权评价该订单");
        }
        
        // 2. 检查订单是否已完成
        if (!order.getStatus().equals(Constants.ORDER_STATUS_COMPLETED)) {
            throw new BusinessException(4003, "订单未完成，无法评价");
        }
        
        // 3. 检查是否已评价
        BizReview existingReview = reviewMapper.selectByOrderId(review.getOrderId());
        if (existingReview != null) {
            throw new BusinessException(4004, "该订单已评价");
        }
        
        // 4. 设置评价信息
        review.setUserId(userId);
        review.setMerchantId(order.getMerchantId());
        review.setCreateTime(LocalDateTime.now());
        
        // 5. 保存评价
        reviewMapper.insert(review);
    }

    @Override
    public void deleteReview(Long id, Long userId, Integer role) {
        BizReview review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(4001, "评价不存在");
        }
        
        // 只有评价本人或管理员可以删除
        if (!review.getUserId().equals(userId) && !role.equals(Constants.USER_ROLE_ADMIN)) {
            throw new BusinessException(4002, "无权删除该评价");
        }
        
        reviewMapper.deleteById(id);
    }
}
