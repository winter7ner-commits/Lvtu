package com.bitzh.lvtu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
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
    
    // 从服务详情表带出的表单数据
    private Long detailId;
    private Object formData; 
}
