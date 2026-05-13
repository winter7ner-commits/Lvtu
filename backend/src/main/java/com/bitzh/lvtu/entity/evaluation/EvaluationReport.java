package com.bitzh.lvtu.entity.evaluation;

import com.bitzh.lvtu.enums.evaluation.ReportReason;
import com.bitzh.lvtu.enums.evaluation.ReportStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价举报实体类
 * 对应数据库表: evaluation_report
 */
@Data
public class EvaluationReport {
    private Long reportId;
    private Long evaluationId;
    private Long reporterId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdTime;
}
