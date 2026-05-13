package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.Order;
import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.mapper.OrderMapper;
import com.bitzh.lvtu.mapper.PaymentMapper;
import com.bitzh.lvtu.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PaymentMapper paymentMapper;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (order.getServiceTypeId() == null) {
            throw new IllegalArgumentException("服务类型不能为空");
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("待接单");
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }

        orderMapper.insert(order);
        if (order.getFormData() != null && !order.getFormData().isBlank()) {
            orderMapper.insertDetail(order);
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("未支付");
        paymentMapper.insert(payment);

        return orderMapper.selectDetailByOrderId(order.getOrderId());
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectDetailByOrderId(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public boolean updateOrderStatus(Long id, String status) {
        Order order = new Order();
        order.setOrderId(id);
        order.setStatus(status);
        return orderMapper.update(order) > 0;
    }
}
