package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizFavorite {
    private Long id;
    private Long userId;
    private Long merchantId;
    private LocalDateTime createTime;
}
