package com.bitzh.lvtu.dto.evaluation.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价响应DTO
 */
@Data
public class EvaluationResponse {
    private Long evaluationId;
    private Long orderId;
    private Long userId;
    private String userName;
    private Long lawyerId;
    private Integer professionalScore;
    private Integer responsivenessScore;
    private Integer attitudeScore;
    private BigDecimal totalScore;
    private String content;
    private Boolean isAnonymous;
    private String status;
    private Integer reportCount;
    private Boolean isFeatured;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
