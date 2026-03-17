package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.entity.BizOrder;
import com.jswm.service.MerchantService;
import com.jswm.service.OrderService;
import com.jswm.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/merchant/orders")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public Result<List<BizOrder>> getMyOrders(@RequestHeader("Authorization") String token,
                                               @RequestParam(required = false) Integer status) {
        Long userId = JwtUtils.getUserId(token);
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return Result.error("未找到商家信息");
        }
        List<BizOrder> list = orderService.getOrderList(null, merchant.getId(), status);
        return Result.success(list);
    }

    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@RequestHeader("Authorization") String token,
                                     @PathVariable Long id) {
        Long userId = JwtUtils.getUserId(token);
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return Result.error("未找到商家信息");
        }
        // 验证订单是否属于当前商家
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.acceptOrder(id);
        return Result.success("接单成功", null);
    }

    @PostMapping("/{id}/deliver")
    public Result<Void> deliverOrder(@RequestHeader("Authorization") String token,
                                      @PathVariable Long id) {
        Long userId = JwtUtils.getUserId(token);
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return Result.error("未找到商家信息");
        }
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.deliverOrder(id);
        return Result.success("开始配送", null);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        Long userId = JwtUtils.getUserId(token);
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return Result.error("未找到商家信息");
        }
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.completeOrder(id);
        return Result.success("订单已完成", null);
    }
}
