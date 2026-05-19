package com.bitzh.lvtu.dto.evaluation.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报响应DTO
 */
@Data
public class ReportResponse {
    private Long reportId;
    private Long evaluationId;
    private Long reporterId;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdTime;
}
