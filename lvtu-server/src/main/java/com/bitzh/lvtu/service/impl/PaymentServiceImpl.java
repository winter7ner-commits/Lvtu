package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.mapper.PaymentMapper;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
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
            serviceOrderMapper.updateStatusWithCurrent(existingPayment.getOrderId(), "待支付", "待接单");
        }
        return updated;
    }
}
