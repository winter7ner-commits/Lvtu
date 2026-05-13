package com.bitzh.lvtu.dto.evaluation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员操作请求DTO
 */
@Data
public class AdminActionRequest {
    @NotNull(message = ""评价ID不能为空"")
    private Long evaluationId;
    
    @NotBlank(message = ""操作类型不能为空"")
    private String actionType;
    
    @NotBlank(message = ""操作原因不能为空"")
    private String reason;
}
