package com.jswm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BizDish {
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;
    private String description;
    private Integer stock;
    private Integer sales;
    private BigDecimal rating;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
