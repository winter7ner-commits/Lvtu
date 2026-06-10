package com.bitzh.lvtu.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long orderId;
    private Long userId;
    private Long lawyerId;
    private Long targetLawyerId;
    private String assignmentType;
    private Integer serviceTypeId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private Long detailId;
    private String formData;
}
