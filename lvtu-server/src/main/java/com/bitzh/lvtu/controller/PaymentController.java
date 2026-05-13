package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.Payment;
import com.bitzh.lvtu.service.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器 (Payment Controller)
 * 提供支付相关的API接口
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    /**
     * 发起支付
     * @param payment 支付信息
     * @return 返回支付详情
     */
    @PostMapping
    public ApiResponse<Payment> createPayment(@RequestBody Payment payment) {
        Payment createdPayment = paymentService.createPayment(payment);
        return ApiResponse.success(createdPayment);
    }

    /**
     * 获取支付状态或详情
     * @param id 支付ID
     * @return 支付实体信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ApiResponse.success(payment);
    }

    /**
     * 根据订单ID获取支付记录
     * @param orderId 订单ID
     * @return 支付信息
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<Payment> getPaymentByOrderId(@PathVariable("orderId") Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ApiResponse.success(payment);
    }

    /**
     * 支付回调/更新支付状态
     * @param id 支付ID
     * @param status 支付状态 (如: SUCCESS, FAILED)
     * @return 成功或失败布尔值
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updatePaymentStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        boolean result = paymentService.updatePaymentStatus(id, status);
        return ApiResponse.success(result);
    }
}
