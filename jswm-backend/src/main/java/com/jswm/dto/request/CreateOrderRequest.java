package com.jswm.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRequest {
    private Long merchantId;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "配送地址不能为空")
    private String deliveryAddress;

    private String remark;
}
