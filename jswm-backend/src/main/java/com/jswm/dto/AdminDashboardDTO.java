package com.jswm.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminDashboardDTO {
    // 统计数据
    private Long totalUsers;
    private Long totalMerchants;
    private Long totalOrders;
    private BigDecimal totalRevenue;

    // 订单趋势
    private List<OrderTrendItem> orderTrend;

    // 商家分类统计
    private List<CategoryStat> categoryStats;

    // 最近活动
    private List<RecentActivity> recentActivities;

    @Data
    public static class OrderTrendItem {
        private String date;
        private Integer count;
    }

    @Data
    public static class CategoryStat {
        private String name;
        private Integer count;
        private Integer percentage;
    }

    @Data
    public static class RecentActivity {
        private String type;
        private String text;
        private String time;
    }
}
