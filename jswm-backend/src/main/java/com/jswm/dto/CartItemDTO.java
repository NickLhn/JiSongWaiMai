package com.jswm.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long id;
    private Long userId;
    private Long dishId;
    private Long merchantId;
    private Integer quantity;
    private Integer checked;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 菜品信息
    private String dishName;
    private BigDecimal dishPrice;
    private String dishImage;

    // 商家信息
    private String merchantName;
}
