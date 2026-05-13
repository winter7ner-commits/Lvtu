package com.bitzh.lvtu.entity.evaluation;

import com.bitzh.lvtu.enums.evaluation.EvaluationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价实体类
 * 对应数据库表: evaluation
 */
@Data
public class Evaluation {
    private Long evaluationId;
    private Long orderId;
    private Long userId;
    private Long lawyerId;
    private Integer professionalScore;
    private Integer responsivenessScore;
    private Integer attitudeScore;
    private BigDecimal totalScore;
    private String content;
    private Boolean isAnonymous;
    private EvaluationStatus status;
    private Integer reportCount;
    private Boolean isFeatured;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
