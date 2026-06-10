package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.LawyerProfile;
import com.bitzh.lvtu.mapper.LawyerProfileMapper;
import com.bitzh.lvtu.mapper.PaymentMapper;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.service.NotificationService;
import com.bitzh.lvtu.service.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentMapper paymentMapper;

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private LawyerProfileMapper lawyerProfileMapper;

    @Resource
    private NotificationService notificationService;

    @Override
    public Payment createPayment(Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("未支付");
        }
        paymentMapper.insert(payment);
        return paymentMapper.selectById(payment.getPaymentId());
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentMapper.selectById(id);
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        List<Payment> payments = paymentMapper.selectByOrderId(orderId);
        return payments.isEmpty() ? null : payments.get(0);
    }

    @Override
    @Transactional
    public boolean updatePaymentStatus(Long id, String status) {
        Payment existingPayment = paymentMapper.selectById(id);
        if (existingPayment == null) {
            return false;
        }

        Payment payment = new Payment();
        payment.setPaymentId(id);
        payment.setStatus(status);
        if ("已支付".equals(status)) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        boolean updated = paymentMapper.update(payment) > 0;
        if (updated && "已支付".equals(status)) {
            int orderUpdated = serviceOrderMapper.updateStatusWithCurrent(existingPayment.getOrderId(), "待支付", "待接单");
            if (orderUpdated > 0) {
                notifyDirectOrderLawyer(existingPayment.getOrderId());
            }
        }
        return updated;
    }

    private void notifyDirectOrderLawyer(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectByOrderId(orderId);
        if (order == null || !"DIRECT".equals(order.getAssignmentType()) || order.getTargetLawyerId() == null) {
            return;
        }
        LawyerProfile lawyer = lawyerProfileMapper.selectByLawyerId(order.getTargetLawyerId());
        if (lawyer == null || lawyer.getUserId() == null) {
            return;
        }
        notificationService.create(
                lawyer.getUserId(),
                "DIRECT_ORDER_ASSIGNED",
                "收到用户指定咨询订单",
                "用户指定由您处理订单 #" + orderId + " 的在线法律咨询，请及时查看并接单。",
                orderId,
                "/lawyer/orders/" + orderId
        );
    }
}
