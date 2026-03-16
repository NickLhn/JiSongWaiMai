package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysSetting {
    private Long id;
    private String settingKey;
    private String settingValue;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
