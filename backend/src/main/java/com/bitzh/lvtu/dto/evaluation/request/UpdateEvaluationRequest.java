package com.bitzh.lvtu.dto.evaluation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 更新评价请求DTO
 */
@Data
public class UpdateEvaluationRequest {
    @Min(value = 1, message = ""专业度评分必须在1-5分之间"")
    @Max(value = 5, message = ""专业度评分必须在1-5分之间"")
    private Integer professionalScore;
    
    @Min(value = 1, message = ""响应速度评分必须在1-5分之间"")
    @Max(value = 5, message = ""响应速度评分必须在1-5分之间"")
    private Integer responsivenessScore;
    
    @Min(value = 1, message = ""服务态度评分必须在1-5分之间"")
    @Max(value = 5, message = ""服务态度评分必须在1-5分之间"")
    private Integer attitudeScore;
    
    private String content;
    
    private Boolean isAnonymous;
}
