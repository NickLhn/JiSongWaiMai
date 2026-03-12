package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizCategory {
    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
