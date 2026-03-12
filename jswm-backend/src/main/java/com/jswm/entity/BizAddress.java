package com.jswm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizAddress {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private String label;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
