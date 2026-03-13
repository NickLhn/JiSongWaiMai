package com.jswm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BizCoupon {
    private Long id;
    private Long userId;
    private String name;
    private Integer type; // 1满减 2折扣 3免配送费
    private BigDecimal minAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private Long merchantId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 0未使用 1已使用 2已过期
    private LocalDateTime useTime;
    private Long orderId;
    private LocalDateTime createTime;
}
