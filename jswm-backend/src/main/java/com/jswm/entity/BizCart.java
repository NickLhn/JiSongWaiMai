package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizCart {
    private Long id;
    private Long userId;
    private Long dishId;
    private Long merchantId;
    private Integer quantity;
    private Integer checked;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
