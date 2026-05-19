package com.bitzh.lvtu.dto.evaluation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建评价请求DTO
 */
@Data
public class CreateEvaluationRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "律师ID不能为空")
    private Long lawyerId;
    
    @Min(value = 1, message = "专业度评分必须在1-5分之间")
    @Max(value = 5, message = "专业度评分必须在1-5分之间")
    @NotNull(message = "专业度评分不能为空")
    private Integer professionalScore;
    
    @Min(value = 1, message = "响应速度评分必须在1-5分之间")
    @Max(value = 5, message = "响应速度评分必须在1-5分之间")
    @NotNull(message = "响应速度评分不能为空")
    private Integer responsivenessScore;
    
    @Min(value = 1, message = "服务态度评分必须在1-5分之间")
    @Max(value = 5, message = "服务态度评分必须在1-5分之间")
    @NotNull(message = "服务态度评分不能为空")
    private Integer attitudeScore;
    
    private String content;
    
    private Boolean isAnonymous;
}
