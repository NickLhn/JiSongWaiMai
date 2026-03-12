package com.jswm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BizMerchant {
    private Long id;
    private Long userId;
    private String shopName;
    private String shopLogo;
    private String shopAddress;
    private String description;
    private String category;
    private String businessHours;
    private BigDecimal rating;
    private Integer sales;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
