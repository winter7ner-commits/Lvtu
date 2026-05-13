package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.Payment;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Payment getPaymentById(Long id);
    Payment getPaymentByOrderId(Long orderId);
    boolean updatePaymentStatus(Long id, String status);
}

