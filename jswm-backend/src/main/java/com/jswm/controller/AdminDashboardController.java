package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.dto.AdminDashboardDTO;
import com.jswm.entity.BizOrder;
import com.jswm.entity.SysUser;
import com.jswm.mapper.BizOrderMapper;
import com.jswm.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private BizOrderMapper orderMapper;

    @GetMapping
    public Result<AdminDashboardDTO> getDashboardData() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // 获取所有用户
        List<SysUser> allUsers = userMapper.selectAll();

        // 统计总用户数（学生）
        long totalUsers = allUsers.stream()
                .filter(u -> u.getRole() == 0)
                .count();
        dashboard.setTotalUsers(totalUsers);

        // 统计入驻商家数
        long totalMerchants = allUsers.stream()
                .filter(u -> u.getRole() == 1)
                .count();
        dashboard.setTotalMerchants(totalMerchants);

        // 获取所有订单
        List<BizOrder> allOrders = orderMapper.selectList(null, null, null);

        // 统计总订单数
        dashboard.setTotalOrders((long) allOrders.size());

        // 统计总交易额（已支付的订单）
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() >= 1) // 已支付及以上状态
                .map(BizOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalRevenue(totalRevenue);

        // 订单趋势（最近7天）
        List<AdminDashboardDTO.OrderTrendItem> orderTrend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            long count = allOrders.stream()
                    .filter(o -> o.getCreateTime().isAfter(dayStart) && o.getCreateTime().isBefore(dayEnd))
                    .count();

            AdminDashboardDTO.OrderTrendItem item = new AdminDashboardDTO.OrderTrendItem();
            item.setDate(date.format(formatter));
            item.setCount((int) count);
            orderTrend.add(item);
        }
        dashboard.setOrderTrend(orderTrend);

        // 商家分类统计（简化处理，按商家名称分组模拟）
        List<AdminDashboardDTO.CategoryStat> categoryStats = new ArrayList<>();
        // 实际项目中应该从商家分类表获取，这里简化处理
        categoryStats.add(new AdminDashboardDTO.CategoryStat() {{
            setName("中式快餐");
            setCount((int) (totalMerchants * 0.4));
            setPercentage(40);
        }});
        categoryStats.add(new AdminDashboardDTO.CategoryStat() {{
            setName("西式快餐");
            setCount((int) (totalMerchants * 0.25));
            setPercentage(25);
        }});
        categoryStats.add(new AdminDashboardDTO.CategoryStat() {{
            setName("饮品甜点");
            setCount((int) (totalMerchants * 0.20));
            setPercentage(20);
        }});
        categoryStats.add(new AdminDashboardDTO.CategoryStat() {{
            setName("其他");
            setCount((int) (totalMerchants * 0.15));
            setPercentage(15);
        }});
        dashboard.setCategoryStats(categoryStats);

        // 最近活动（从订单和用户数据生成）
        List<AdminDashboardDTO.RecentActivity> recentActivities = new ArrayList<>();

        // 添加最近的用户注册活动
        allUsers.stream()
                .filter(u -> u.getCreateTime() != null)
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(2)
                .forEach(u -> {
                    AdminDashboardDTO.RecentActivity activity = new AdminDashboardDTO.RecentActivity();
                    activity.setType("user");
                    activity.setText("新用户 " + u.getRealName() + " 注册成功");
                    activity.setTime(getTimeAgo(u.getCreateTime()));
                    recentActivities.add(activity);
                });

        // 添加最近的订单活动
        allOrders.stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(3)
                .forEach(o -> {
                    AdminDashboardDTO.RecentActivity activity = new AdminDashboardDTO.RecentActivity();
                    if (o.getStatus() >= 1) {
                        activity.setType("order");
                        activity.setText("新订单 " + o.getOrderNo() + " 已支付");
                    } else {
                        activity.setType("order");
                        activity.setText("新订单 " + o.getOrderNo() + " 待支付");
                    }
                    activity.setTime(getTimeAgo(o.getCreateTime()));
                    recentActivities.add(activity);
                });

        dashboard.setRecentActivities(recentActivities);

        return Result.success(dashboard);
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else {
            return days + "天前";
        }
    }
}
