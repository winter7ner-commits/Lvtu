package com.bitzh.lvtu.dto.evaluation.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理日志响应DTO
 */
@Data
public class AdminLogResponse {
    private Long logId;
    private Long evaluationId;
    private Long adminId;
    private String adminName;
    private String actionType;
    private String reason;
    private LocalDateTime createdTime;
}
