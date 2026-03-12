package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.dto.request.CancelOrderRequest;
import com.jswm.dto.request.CreateOrderRequest;
import com.jswm.entity.BizOrder;
import com.jswm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public Result<BizOrder> getOrderById(@PathVariable Long id) {
        BizOrder order = orderService.getOrderById(id);
        return Result.success(order);
    }

    @GetMapping
    public Result<List<BizOrder>> getOrderList(@RequestHeader("Authorization") String token,
                                                 @RequestParam(required = false) Integer status) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        List<BizOrder> list = orderService.getOrderList(userId, null, status);
        return Result.success(list);
    }

    @PostMapping
    public Result<BizOrder> createOrder(@RequestHeader("Authorization") String token,
                                          @RequestBody CreateOrderRequest request) {
        Long userId = com.jswm.utils.JwtUtils.getUserId(token);
        BizOrder order = orderService.createOrder(userId, request.getMerchantId(),
                request.getReceiverName(), request.getReceiverPhone(),
                request.getDeliveryAddress(), request.getRemark());
        return Result.success("创建成功", order);
    }

    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success("支付成功", null);
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody CancelOrderRequest request) {
        orderService.cancelOrder(id, request.getReason());
        return Result.success("取消成功", null);
    }

    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id) {
        orderService.acceptOrder(id);
        return Result.success("接单成功", null);
    }

    @PostMapping("/{id}/deliver")
    public Result<Void> deliverOrder(@PathVariable Long id) {
        orderService.deliverOrder(id);
        return Result.success("配送中", null);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success("订单已完成", null);
    }
}
