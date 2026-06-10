package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.Order;
import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.entity.LawyerProfile;
import com.bitzh.lvtu.mapper.LawyerProfileMapper;
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

    private static final String ASSIGNMENT_PUBLIC = "PUBLIC";
    private static final String ASSIGNMENT_DIRECT = "DIRECT";
    private static final int ONLINE_CONSULT_SERVICE_TYPE = 101;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PaymentMapper paymentMapper;

    @Resource
    private LawyerProfileMapper lawyerProfileMapper;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (order.getServiceTypeId() == null) {
            throw new IllegalArgumentException("服务类型不能为空");
        }
        normalizeAssignment(order);
        order.setStatus("待支付");
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

    private void normalizeAssignment(Order order) {
        String assignmentType = order.getAssignmentType();
        if (assignmentType == null || assignmentType.isBlank()) {
            assignmentType = order.getTargetLawyerId() == null ? ASSIGNMENT_PUBLIC : ASSIGNMENT_DIRECT;
        }
        assignmentType = assignmentType.trim().toUpperCase();

        if (ASSIGNMENT_DIRECT.equals(assignmentType)) {
            if (order.getTargetLawyerId() == null) {
                throw new IllegalArgumentException("指定律师订单必须选择律师");
            }
            if (!Integer.valueOf(ONLINE_CONSULT_SERVICE_TYPE).equals(order.getServiceTypeId())) {
                throw new IllegalArgumentException("当前仅支持在线法律咨询指定律师");
            }
            LawyerProfile lawyer = lawyerProfileMapper.selectByLawyerId(order.getTargetLawyerId());
            if (lawyer == null) {
                throw new IllegalArgumentException("指定律师不存在或暂不可接单");
            }
            order.setAssignmentType(ASSIGNMENT_DIRECT);
            order.setLawyerId(null);
            return;
        }

        if (!ASSIGNMENT_PUBLIC.equals(assignmentType)) {
            throw new IllegalArgumentException("订单分配类型无效");
        }
        order.setAssignmentType(ASSIGNMENT_PUBLIC);
        order.setTargetLawyerId(null);
        order.setLawyerId(null);
    }

    @Override
    public Order getOrderById(Long id) {
        orderMapper.completeEvaluatedPendingOrder(id);
        return orderMapper.selectDetailByOrderId(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        orderMapper.completeEvaluatedPendingOrdersByUserId(userId);
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
