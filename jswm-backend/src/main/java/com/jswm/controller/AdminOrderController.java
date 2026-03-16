package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.BizMerchant;
import com.jswm.entity.BizOrder;
import com.jswm.entity.SysUser;
import com.jswm.mapper.BizMerchantMapper;
import com.jswm.mapper.BizOrderMapper;
import com.jswm.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/orders")
public class AdminOrderController {

    @Autowired
    private BizOrderMapper orderMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private BizMerchantMapper merchantMapper;

    @GetMapping
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取所有订单
        List<BizOrder> allOrders = orderMapper.selectList(null, null, status);

        // 过滤
        List<BizOrder> filteredOrders = allOrders.stream()
                .filter(o -> {
                    if (keyword != null && !keyword.isEmpty()) {
                        boolean matchOrderNo = o.getOrderNo() != null && o.getOrderNo().contains(keyword);
                        boolean matchReceiver = o.getReceiverName() != null && o.getReceiverName().contains(keyword);
                        boolean matchPhone = o.getReceiverPhone() != null && o.getReceiverPhone().contains(keyword);
                        if (!matchOrderNo && !matchReceiver && !matchPhone) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 分页
        int total = filteredOrders.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<BizOrder> pageData = start < total ? filteredOrders.subList(start, end) : List.of();

        // 补充用户和商家信息
        List<Map<String, Object>> list = pageData.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("amount", order.getPayAmount());
            map.put("status", order.getStatus());
            map.put("createTime", order.getCreateTime());
            map.put("receiverName", order.getReceiverName());
            map.put("receiverPhone", order.getReceiverPhone());
            map.put("deliveryAddress", order.getDeliveryAddress());
            map.put("remark", order.getRemark());

            // 获取用户信息
            SysUser user = userMapper.selectById(order.getUserId());
            if (user != null) {
                map.put("userName", user.getRealName() != null ? user.getRealName() : user.getUsername());
                map.put("userPhone", user.getPhone());
            } else {
                map.put("userName", "未知用户");
                map.put("userPhone", "-");
            }

            // 获取商家信息
            BizMerchant merchant = merchantMapper.selectById(order.getMerchantId());
            if (merchant != null) {
                map.put("merchantName", merchant.getShopName());
            } else {
                map.put("merchantName", "未知商家");
            }

            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BizOrder> getOrderDetail(@PathVariable Long id) {
        BizOrder order = orderMapper.selectById(id);
        return Result.success(order);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        BizOrder order = new BizOrder();
        order.setId(id);
        order.setStatus(status);
        orderMapper.updateById(order);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        BizOrder order = new BizOrder();
        order.setId(id);
        order.setStatus(5); // 已取消
        order.setCancelReason(reason);
        orderMapper.updateById(order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderMapper.deleteById(id);
        return Result.success();
    }
}
