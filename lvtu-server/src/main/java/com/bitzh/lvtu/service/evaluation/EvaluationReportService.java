package com.bitzh.lvtu.service.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateReportRequest;
import com.bitzh.lvtu.dto.evaluation.response.ReportResponse;

import java.util.List;

/**
 * 评价举报服务接口
 */
public interface EvaluationReportService {

    /**
     * 创建举报
     */
    ReportResponse createReport(CreateReportRequest request, Long userId);

    /**
     * 获取评价的举报记录
     */
    List<ReportResponse> getReportsByEvaluationId(Long evaluationId);
}
