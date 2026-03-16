package com.jswm.service.impl;

import com.jswm.entity.BizCart;
import com.jswm.entity.BizDish;
import com.jswm.entity.BizOrder;
import com.jswm.entity.BizOrderItem;
import com.jswm.exception.BusinessException;
import com.jswm.mapper.BizCartMapper;
import com.jswm.mapper.BizDishMapper;
import com.jswm.mapper.BizOrderItemMapper;
import com.jswm.mapper.BizOrderMapper;
import com.jswm.service.OrderService;
import com.jswm.utils.OrderNoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private BizOrderMapper orderMapper;

    @Autowired
    private BizOrderItemMapper orderItemMapper;

    @Autowired
    private BizCartMapper cartMapper;

    @Autowired
    private BizDishMapper dishMapper;

    @Override
    public BizOrder getOrderById(Long id) {
        BizOrder order = orderMapper.selectById(id);
        if (order != null) {
            List<BizOrderItem> items = orderItemMapper.selectByOrderId(id);
            order.setItems(items);
        }
        return order;
    }

    @Override
    public BizOrder getOrderByOrderNo(String orderNo) {
        BizOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<BizOrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            order.setItems(items);
        }
        return order;
    }

    @Override
    public List<BizOrder> getOrderList(Long userId, Long merchantId, Integer status) {
        List<BizOrder> orders = orderMapper.selectList(userId, merchantId, status);
        if (orders != null && !orders.isEmpty()) {
            for (BizOrder order : orders) {
                List<BizOrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                order.setItems(items);
            }
        }
        return orders;
    }

    @Override
    @Transactional
    public BizOrder createOrder(Long userId, Long merchantId, String receiverName, String receiverPhone, String deliveryAddress, String remark) {
        // 1. 获取购物车商品
        List<BizCart> cartItems = cartMapper.selectByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException(3003, "购物车为空");
        }

        // 2. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BizOrderItem> orderItems = new ArrayList<>();

        for (BizCart cartItem : cartItems) {
            // 只处理当前商家的商品
            if (!cartItem.getMerchantId().equals(merchantId)) {
                continue;
            }

            BizDish dish = dishMapper.selectById(cartItem.getDishId());
            if (dish == null) {
                throw new BusinessException(3004, "商品不存在: " + cartItem.getDishId());
            }

            BigDecimal subtotal = dish.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            BizOrderItem orderItem = new BizOrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        if (orderItems.isEmpty()) {
            throw new BusinessException(3003, "该商家购物车为空");
        }

        // 3. 创建订单
        BizOrder order = new BizOrder();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setRemark(remark);
        order.setStatus(0);
        order.setPayType(3);
        orderMapper.insert(order);

        // 4. 创建订单明细
        for (BizOrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.insertBatch(orderItems);

        // 5. 清空购物车中该商家的商品
        for (BizCart cartItem : cartItems) {
            if (cartItem.getMerchantId().equals(merchantId)) {
                cartMapper.deleteById(cartItem.getId());
            }
        }

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
