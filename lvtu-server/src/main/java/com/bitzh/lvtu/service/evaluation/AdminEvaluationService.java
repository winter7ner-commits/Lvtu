package com.bitzh.lvtu.service.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.AdminActionRequest;
import com.bitzh.lvtu.dto.evaluation.response.AdminLogResponse;

import java.util.List;

/**
 * 管理员评价服务接口
 */
public interface AdminEvaluationService {

    /**
     * 处理评价（隐藏/删除/精选等）
     */
    void handleEvaluation(AdminActionRequest request, Long adminId);

    /**
     * 获取评价的操作日志
     */
    List<AdminLogResponse> getLogsByEvaluationId(Long evaluationId);
}
