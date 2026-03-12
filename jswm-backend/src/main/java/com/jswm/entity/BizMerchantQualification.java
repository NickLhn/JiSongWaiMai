package com.jswm.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BizMerchantQualification {
    private Long id;
    private Long merchantId;
    private String qualificationType;
    private String qualificationNo;
    private String qualificationImage;
    private LocalDate expireDate;
    private Integer status;
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
