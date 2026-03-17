package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSetting {
    private Long id;
    private Long userId;
    private Integer orderNotify;
    private Integer promotionNotify;
    private Integer systemNotify;
    private Integer darkMode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
