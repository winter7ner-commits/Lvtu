package com.bitzh.lvtu.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long orderId;
    private Long userId;
    private Long lawyerId;
    private Long serviceTypeId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String formData;

    // 关联的律师名称（用于 JOIN 查询，不映射到数据库）
    private String lawyerName;
}
