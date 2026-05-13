package com.bitzh.lvtu.entity.evaluation;

import com.bitzh.lvtu.enums.evaluation.ActionType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员评价操作日志实体类
 * 对应数据库表: admin_evaluation_log
 */
@Data
public class AdminEvaluationLog {
    private Long logId;
    private Long evaluationId;
    private Long adminId;
    private ActionType actionType;
    private String reason;
    private LocalDateTime createdTime;
}
