package com.jswm.service.impl;

import com.jswm.entity.BizOrder;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizOrderMapper;
import com.jswm.service.OrderService;
import com.jswm.utils.OrderNoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private BizOrderMapper orderMapper;

    @Override
    public BizOrder getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public BizOrder getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<BizOrder> getOrderList(Long userId, Long merchantId, Integer status) {
        return orderMapper.selectList(userId, merchantId, status);
    }

    @Override
    public BizOrder createOrder(Long userId, Long merchantId, String receiverName, String receiverPhone, String deliveryAddress, String remark) {
        BizOrder order = new BizOrder();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setRemark(remark);
        order.setStatus(0);
        order.setPayType(3);
        orderMapper.insert(order);
        return order;
    }

    @Override
    public void payOrder(Long id) {
        BizOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(3002, "订单状态异常");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void cancelOrder(Long id, String reason) {
        BizOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(3002, "订单状态异常，无法取消");
        }
        order.setStatus(5);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void acceptOrder(Long id) {
        BizOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(3002, "订单状态异常");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    @Override
    public void deliverOrder(Long id) {
        BizOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(3002, "订单状态异常");
        }
        order.setStatus(3);
        order.setDeliverTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void completeOrder(Long id) {
        BizOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException(3002, "订单状态异常");
        }
        order.setStatus(4);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }
}
