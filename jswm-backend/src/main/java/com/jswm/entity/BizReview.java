package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizReview {
    private Long id;
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long dishId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
