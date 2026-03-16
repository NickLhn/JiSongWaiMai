package com.jswm.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MerchantDashboardDTO {
    // 统计数据
    private BigDecimal todaySales;
    private Integer todayOrders;
    private Integer newCustomers;
    private BigDecimal rating;

    // 订单状态统计
    private Integer pendingOrders;
    private Integer processingOrders;
    private Integer deliveringOrders;
    private Integer completedOrders;

    // 销售趋势数据
    private List<SalesTrendItem> salesTrend;

    // 最新订单
    private List<RecentOrderItem> recentOrders;

    @Data
    public static class SalesTrendItem {
        private String date;
        private BigDecimal amount;
    }

    @Data
    public static class RecentOrderItem {
        private Long id;
        private String orderNo;
        private String customerName;
        private String items;
        private BigDecimal amount;
        private Integer status;
    }
}
