package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.entity.BizReview;
import com.jswm.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取订单的评价
     */
    @GetMapping("/order/{orderId}")
    public Result<BizReview> getReviewByOrderId(@PathVariable Long orderId) {
        BizReview review = reviewService.getReviewByOrderId(orderId);
        return Result.success(review);
    }

    /**
     * 获取商家的评价列表
     */
    @GetMapping("/merchant/{merchantId}")
    public Result<List<BizReview>> getMerchantReviews(@PathVariable Long merchantId,
                                                       @RequestParam(required = false) Integer rating,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        List<BizReview> list = reviewService.getMerchantReviews(merchantId, rating, page, pageSize);
        return Result.success(list);
    }

    /**
     * 获取商家的评价统计
     */
    @GetMapping("/merchant/{merchantId}/stats")
    public Result<Map<String, Object>> getMerchantReviewStats(@PathVariable Long merchantId) {
        Map<String, Object> stats = reviewService.getMerchantReviewStats(merchantId);
        return Result.success(stats);
    }

    /**
     * 获取我的评价列表
     */
    @GetMapping("/my")
    public Result<List<BizReview>> getMyReviews(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        Long userId = AuthContext.getUserId();
        List<BizReview> list = reviewService.getUserReviews(userId, page, pageSize);
        return Result.success(list);
    }

    /**
     * 提交评价
     */
    @PostMapping
    public Result<Void> submitReview(@Valid @RequestBody BizReview review) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        Long userId = AuthContext.getUserId();
        reviewService.submitReview(userId, review);
        return Result.success("评价成功", null);
    }

    /**
     * 删除评价（仅本人或管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT, Constants.USER_ROLE_ADMIN);
        Long userId = AuthContext.getUserId();
        Integer role = AuthContext.getRole();
        reviewService.deleteReview(id, userId, role);
        return Result.success("删除成功", null);
    }
}
