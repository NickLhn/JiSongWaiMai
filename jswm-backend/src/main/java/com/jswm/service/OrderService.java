package com.jswm.service;

import com.jswm.entity.BizOrder;
import java.util.List;

public interface OrderService {
    BizOrder getOrderById(Long id);
    BizOrder getOrderByOrderNo(String orderNo);
    List<BizOrder> getOrderList(Long userId, Long merchantId, Integer status);
    BizOrder createOrder(Long userId, Long merchantId, String receiverName, String receiverPhone, String deliveryAddress, String remark);
    void payOrder(Long id);
    void cancelOrder(Long id, String reason);
    void acceptOrder(Long id);
    void deliverOrder(Long id);
    void completeOrder(Long id);
}
