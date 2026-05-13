package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.mapper.PaymentMapper;
import com.bitzh.lvtu.service.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentMapper paymentMapper;

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
    public boolean updatePaymentStatus(Long id, String status) {
        Payment payment = new Payment();
        payment.setPaymentId(id);
        payment.setStatus(status);
        if ("已支付".equals(status)) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        return paymentMapper.update(payment) > 0;
    }
}
