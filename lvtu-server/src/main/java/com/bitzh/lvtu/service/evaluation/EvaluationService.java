package com.bitzh.lvtu.service.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.request.UpdateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.response.EvaluationResponse;
import com.github.pagehelper.PageInfo;

/**
 * 评价服务接口
 */
public interface EvaluationService {

    /**
     * 创建评价
     */
    EvaluationResponse createEvaluation(CreateEvaluationRequest request, Long userId);

    /**
     * 更新评价
     */
    EvaluationResponse updateEvaluation(Long evaluationId, UpdateEvaluationRequest request, Long userId);

    /**
     * 获取评价详情
     */
    EvaluationResponse getEvaluationById(Long evaluationId);

    /**
     * 获取律师的评价列表（分页）
     */
    PageInfo<EvaluationResponse> getEvaluationsByLawyerId(Long lawyerId, int page, int size);
}
