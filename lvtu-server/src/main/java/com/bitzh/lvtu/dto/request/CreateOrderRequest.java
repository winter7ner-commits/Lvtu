package com.bitzh.lvtu.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private Long userId;
    private Long lawyerId;
    private Integer serviceTypeId;
    private BigDecimal totalAmount;
    private String status;
    private Object formData;
}
