package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.entity.BizOrder;
import com.jswm.exception.BusinessException;
import com.jswm.service.MerchantService;
import com.jswm.service.OrderService;
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
    public Result<List<BizOrder>> getMyOrders(@RequestParam(required = false) Integer status) {
        BizMerchant merchant = requireCurrentMerchant();
        List<BizOrder> list = orderService.getOrderList(null, merchant.getId(), status);
        return Result.success(list);
    }

    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id) {
        BizMerchant merchant = requireCurrentMerchant();
        // 验证订单是否属于当前商家
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.acceptOrder(id);
        return Result.success("接单成功", null);
    }

    @PostMapping("/{id}/deliver")
    public Result<Void> deliverOrder(@PathVariable Long id) {
        BizMerchant merchant = requireCurrentMerchant();
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.deliverOrder(id);
        return Result.success("开始配送", null);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        BizMerchant merchant = requireCurrentMerchant();
        BizOrder order = orderService.getOrderById(id);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            return Result.error("订单不存在");
        }
        orderService.completeOrder(id);
        return Result.success("订单已完成", null);
    }

    private BizMerchant requireCurrentMerchant() {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT);
        BizMerchant merchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
        if (merchant == null) {
            throw new BusinessException(2002, "未找到商家信息");
        }
        return merchant;
    }
}
