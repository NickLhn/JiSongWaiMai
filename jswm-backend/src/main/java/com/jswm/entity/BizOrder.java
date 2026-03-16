package com.jswm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BizOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String remark;
    private Integer status;
    private Integer payType;
    private LocalDateTime payTime;
    private LocalDateTime deliverTime;
    private LocalDateTime completeTime;
    private String cancelReason;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;

    private List<BizOrderItem> items;
}
