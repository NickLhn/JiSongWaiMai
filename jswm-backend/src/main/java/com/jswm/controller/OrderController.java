package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Constants;
import com.jswm.common.Result;
import com.jswm.dto.request.CancelOrderRequest;
import com.jswm.dto.request.CreateOrderRequest;
import com.jswm.entity.BizMerchant;
import com.jswm.entity.BizOrder;
import com.jswm.exception.BusinessException;
import com.jswm.service.MerchantService;
import com.jswm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/{id}")
    public Result<BizOrder> getOrderById(@PathVariable Long id) {
        BizOrder order = orderService.getOrderById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        validateOrderAccess(order);
        return Result.success(order);
    }

    @GetMapping
    public Result<List<BizOrder>> getOrderList(@RequestParam(required = false) Integer status) {
        Long userId = AuthContext.getUserId();
        Integer role = AuthContext.getRole();
        List<BizOrder> list;
        if (Constants.USER_ROLE_ADMIN.equals(role)) {
            list = orderService.getOrderList(null, null, status);
        } else if (Constants.USER_ROLE_MERCHANT.equals(role)) {
            BizMerchant merchant = requireMerchant(userId);
            list = orderService.getOrderList(null, merchant.getId(), status);
        } else {
            list = orderService.getOrderList(userId, null, status);
        }
        return Result.success(list);
    }

    @PostMapping
    public Result<BizOrder> createOrder(@RequestBody CreateOrderRequest request) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        Long userId = AuthContext.getUserId();
        BizOrder order = orderService.createOrder(userId, request.getMerchantId(),
                request.getReceiverName(), request.getReceiverPhone(),
                request.getDeliveryAddress(), request.getRemark());
        return Result.success("创建成功", order);
    }

    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        BizOrder order = orderService.getOrderById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (!order.getUserId().equals(AuthContext.getUserId())) {
            throw new BusinessException(403, "无权支付该订单");
        }
        orderService.payOrder(id);
        return Result.success("支付成功", null);
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody CancelOrderRequest request) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        BizOrder order = orderService.getOrderById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (!order.getUserId().equals(AuthContext.getUserId())) {
            throw new BusinessException(403, "无权取消该订单");
        }
        orderService.cancelOrder(id, request.getReason());
        return Result.success("取消成功", null);
    }

    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id) {
        validateMerchantOrderAccess(id);
        orderService.acceptOrder(id);
        return Result.success("接单成功", null);
    }

    @PostMapping("/{id}/deliver")
    public Result<Void> deliverOrder(@PathVariable Long id) {
        validateMerchantOrderAccess(id);
        orderService.deliverOrder(id);
        return Result.success("配送中", null);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        validateMerchantOrderAccess(id);
        orderService.completeOrder(id);
        return Result.success("订单已完成", null);
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        AuthContext.requireRole(Constants.USER_ROLE_STUDENT);
        BizOrder order = orderService.getOrderById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (!order.getUserId().equals(AuthContext.getUserId())) {
            throw new BusinessException(403, "无权确认该订单");
        }
        orderService.completeOrder(id);
        return Result.success("确认收货成功", null);
    }

    private void validateOrderAccess(BizOrder order) {
        Integer role = AuthContext.getRole();
        Long userId = AuthContext.getUserId();
        if (Constants.USER_ROLE_ADMIN.equals(role)) {
            return;
        }
        if (Constants.USER_ROLE_STUDENT.equals(role) && order.getUserId().equals(userId)) {
            return;
        }
        if (Constants.USER_ROLE_MERCHANT.equals(role)) {
            BizMerchant merchant = requireMerchant(userId);
            if (merchant.getId().equals(order.getMerchantId())) {
                return;
            }
        }
        throw new BusinessException(403, "无权访问该订单");
    }

    private void validateMerchantOrderAccess(Long orderId) {
        AuthContext.requireRole(Constants.USER_ROLE_MERCHANT, Constants.USER_ROLE_ADMIN);
        if (Constants.USER_ROLE_ADMIN.equals(AuthContext.getRole())) {
            return;
        }
        BizOrder order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        BizMerchant merchant = requireMerchant(AuthContext.getUserId());
        if (!merchant.getId().equals(order.getMerchantId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
    }

    private BizMerchant requireMerchant(Long userId) {
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            throw new BusinessException(2002, "未找到商家信息");
        }
        return merchant;
    }
}
