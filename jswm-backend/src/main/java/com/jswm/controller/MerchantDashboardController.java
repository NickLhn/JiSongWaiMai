package com.jswm.controller;

import com.jswm.common.AuthContext;
import com.jswm.common.Result;
import com.jswm.dto.MerchantDashboardDTO;
import com.jswm.entity.BizOrder;
import com.jswm.entity.BizMerchant;
import com.jswm.exception.BusinessException;
import com.jswm.service.MerchantService;
import com.jswm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/merchant/dashboard")
public class MerchantDashboardController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public Result<MerchantDashboardDTO> getDashboardData() {
        BizMerchant merchant = merchantService.getMerchantByUserId(AuthContext.getUserId());
        if (merchant == null) {
            throw new BusinessException(2002, "未找到店铺信息");
        }
        Long merchantId = merchant.getId();
        
        // 获取商家所有订单
        List<BizOrder> allOrders = orderService.getOrderList(null, merchantId, null);
        
        MerchantDashboardDTO dashboard = new MerchantDashboardDTO();
        
        // 计算今日数据
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        
        List<BizOrder> todayOrderList = allOrders.stream()
            .filter(o -> o.getCreateTime().isAfter(todayStart) && o.getCreateTime().isBefore(todayEnd))
            .collect(Collectors.toList());
        
        // 今日销售额（已支付的订单）
        BigDecimal todaySales = todayOrderList.stream()
            .filter(o -> o.getStatus() >= 1) // 已支付及以上状态
            .map(BizOrder::getPayAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTodaySales(todaySales);
        
        // 今日订单数
        dashboard.setTodayOrders(todayOrderList.size());
        
        // 新增客户（简化处理：今日下单的不同用户数）
        long newCustomers = todayOrderList.stream()
            .map(BizOrder::getUserId)
            .distinct()
            .count();
        dashboard.setNewCustomers((int) newCustomers);
        
        // 店铺评分（简化处理：固定值或从其他表获取）
        dashboard.setRating(new BigDecimal("4.8"));
        
        // 订单状态统计
        dashboard.setPendingOrders((int) allOrders.stream().filter(o -> o.getStatus() == 1).count());
        dashboard.setProcessingOrders((int) allOrders.stream().filter(o -> o.getStatus() == 2).count());
        dashboard.setDeliveringOrders((int) allOrders.stream().filter(o -> o.getStatus() == 3).count());
        dashboard.setCompletedOrders((int) allOrders.stream().filter(o -> o.getStatus() == 4).count());
        
        // 销售趋势（最近7天）
        List<MerchantDashboardDTO.SalesTrendItem> salesTrend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            BigDecimal daySales = allOrders.stream()
                .filter(o -> o.getCreateTime().isAfter(dayStart) && o.getCreateTime().isBefore(dayEnd))
                .filter(o -> o.getStatus() >= 1)
                .map(BizOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            MerchantDashboardDTO.SalesTrendItem item = new MerchantDashboardDTO.SalesTrendItem();
            item.setDate(date.format(formatter));
            item.setAmount(daySales);
            salesTrend.add(item);
        }
        dashboard.setSalesTrend(salesTrend);
        
        // 最新订单（最近5条）
        List<MerchantDashboardDTO.RecentOrderItem> recentOrders = allOrders.stream()
            .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
            .limit(5)
            .map(order -> {
                MerchantDashboardDTO.RecentOrderItem item = new MerchantDashboardDTO.RecentOrderItem();
                item.setId(order.getId());
                item.setOrderNo(order.getOrderNo());
                item.setCustomerName(order.getReceiverName());
                // 简化处理：显示商品数量
                item.setItems("共" + (order.getItems() != null ? order.getItems().size() : 0) + "件商品");
                item.setAmount(order.getPayAmount());
                item.setStatus(order.getStatus());
                return item;
            })
            .collect(Collectors.toList());
        dashboard.setRecentOrders(recentOrders);
        
        return Result.success(dashboard);
    }
}
