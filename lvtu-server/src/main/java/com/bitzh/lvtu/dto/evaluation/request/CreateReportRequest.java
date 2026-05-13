package com.bitzh.lvtu.dto.evaluation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建举报请求DTO
 */
@Data
public class CreateReportRequest {
    @NotNull(message = "评价ID不能为空")
    private Long evaluationId;
    
    @NotBlank(message = "举报原因不能为空")
    private String reason;
    
    private String description;
}
