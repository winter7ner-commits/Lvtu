package com.bitzh.lvtu.service.impl.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.request.UpdateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.response.EvaluationResponse;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.evaluation.Evaluation;
import com.bitzh.lvtu.enums.evaluation.EvaluationStatus;
import com.bitzh.lvtu.exception.evaluation.EvaluationException;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.mapper.evaluation.EvaluationMapper;
import com.bitzh.lvtu.service.evaluation.EvaluationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationMapper evaluationMapper;
    private final ServiceOrderMapper serviceOrderMapper;

    public EvaluationServiceImpl(EvaluationMapper evaluationMapper, ServiceOrderMapper serviceOrderMapper) {
        this.evaluationMapper = evaluationMapper;
        this.serviceOrderMapper = serviceOrderMapper;
    }

    @Override
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request, Long userId) {
        ServiceOrder order = serviceOrderMapper.selectByOrderId(request.getOrderId());
        validateOrderForEvaluation(order, request.getLawyerId());
        if (!order.getUserId().equals(userId)) {
            throw new EvaluationException("只有下单用户可以提交评价");
        }

        Optional<Evaluation> existingEvaluation = evaluationMapper.findByOrderId(request.getOrderId());
        if (existingEvaluation.isPresent()) {
            completeOrderIfPendingReview(order);
            return convertToResponse(existingEvaluation.get());
        }
        
        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(request.getOrderId());
        evaluation.setUserId(userId);
        evaluation.setLawyerId(request.getLawyerId());
        evaluation.setProfessionalScore(request.getProfessionalScore());
        evaluation.setResponsivenessScore(request.getResponsivenessScore());
        evaluation.setAttitudeScore(request.getAttitudeScore());
        evaluation.setContent(request.getContent());
        evaluation.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
        
        // 计算综合评分
        BigDecimal total = BigDecimal.valueOf(
            (request.getProfessionalScore() + 
             request.getResponsivenessScore() + 
             request.getAttitudeScore()) / 3.0
        ).setScale(1, BigDecimal.ROUND_HALF_UP);
        evaluation.setTotalScore(total);
        
        evaluation.setStatus(EvaluationStatus.NORMAL);
        evaluation.setCreatedTime(LocalDateTime.now());
        evaluation.setUpdatedTime(LocalDateTime.now());
        
        evaluationMapper.insert(evaluation);
        completeOrderIfPendingReview(order);
        return convertToResponse(evaluation);
    }

    private void validateOrderForEvaluation(ServiceOrder order, Long lawyerId) {
        if (order == null) {
            throw new EvaluationException("订单不存在");
        }
        if (order.getUserId() == null) {
            throw new EvaluationException("订单缺少用户信息");
        }
        if (order.getLawyerId() == null || !order.getLawyerId().equals(lawyerId)) {
            throw new EvaluationException("评价律师与订单律师不一致");
        }
        if (!"待评价".equals(order.getStatus()) && !"已完成".equals(order.getStatus())) {
            throw new EvaluationException("当前订单状态不允许评价");
        }
    }

    private void completeOrderIfPendingReview(ServiceOrder order) {
        if ("已完成".equals(order.getStatus())) {
            return;
        }

        int updated = serviceOrderMapper.updateStatusWithCurrent(order.getOrderId(), "待评价", "已完成");
        if (updated == 0) {
            throw new EvaluationException("评价成功后更新订单状态失败");
        }
    }

    @Override
    public EvaluationResponse updateEvaluation(Long evaluationId, UpdateEvaluationRequest request, Long userId) {
        Evaluation evaluation = evaluationMapper.findById(evaluationId)
            .orElseThrow(() -> new EvaluationException("评价不存在"));
            
        // 检查权限
        if (!evaluation.getUserId().equals(userId)) {
            throw new EvaluationException("没有权限修改他人的评价");
        }
        
        if (request.getProfessionalScore() != null) {
            evaluation.setProfessionalScore(request.getProfessionalScore());
        }
        if (request.getResponsivenessScore() != null) {
            evaluation.setResponsivenessScore(request.getResponsivenessScore());
        }
        if (request.getAttitudeScore() != null) {
            evaluation.setAttitudeScore(request.getAttitudeScore());
        }
        if (request.getContent() != null) {
            evaluation.setContent(request.getContent());
        }
        if (request.getIsAnonymous() != null) {
            evaluation.setIsAnonymous(request.getIsAnonymous());
        }
        
        // 重新计算综合评分
        if (request.getProfessionalScore() != null || 
            request.getResponsivenessScore() != null || 
            request.getAttitudeScore() != null) {
            
            int professional = request.getProfessionalScore() != null ? 
                request.getProfessionalScore() : evaluation.getProfessionalScore();
            int responsiveness = request.getResponsivenessScore() != null ? 
                request.getResponsivenessScore() : evaluation.getResponsivenessScore();
            int attitude = request.getAttitudeScore() != null ? 
                request.getAttitudeScore() : evaluation.getAttitudeScore();
                
            BigDecimal total = BigDecimal.valueOf(
                (professional + responsiveness + attitude) / 3.0
            ).setScale(1, BigDecimal.ROUND_HALF_UP);
            evaluation.setTotalScore(total);
        }
        
        evaluation.setUpdatedTime(LocalDateTime.now());
        
        evaluationMapper.update(evaluation);
        return convertToResponse(evaluation);
    }

    @Override
    public EvaluationResponse getEvaluationById(Long evaluationId) {
        Evaluation evaluation = evaluationMapper.findById(evaluationId)
            .orElseThrow(() -> new EvaluationException("评价不存在"));
        return convertToResponse(evaluation);
    }

    @Override
    public PageInfo<EvaluationResponse> getEvaluationsByLawyerId(Long lawyerId, int page, int size) {
        PageHelper.startPage(page, size);
        List<Evaluation> evaluations = evaluationMapper.findByLawyerIdAndStatus(
            lawyerId, EvaluationStatus.NORMAL.getValue());
        
        List<EvaluationResponse> responses = evaluations.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
            
        return new PageInfo<>(responses);
    }
    
    private EvaluationResponse convertToResponse(Evaluation evaluation) {
        EvaluationResponse response = new EvaluationResponse();
        response.setEvaluationId(evaluation.getEvaluationId());
        response.setOrderId(evaluation.getOrderId());
        response.setUserId(evaluation.getUserId());
        response.setLawyerId(evaluation.getLawyerId());
        response.setProfessionalScore(evaluation.getProfessionalScore());
        response.setResponsivenessScore(evaluation.getResponsivenessScore());
        response.setAttitudeScore(evaluation.getAttitudeScore());
        response.setTotalScore(evaluation.getTotalScore());
        response.setContent(evaluation.getContent());
        response.setIsAnonymous(evaluation.getIsAnonymous());
        response.setReportCount(evaluation.getReportCount());
        response.setIsFeatured(evaluation.getIsFeatured());
        response.setCreatedTime(evaluation.getCreatedTime());
        response.setUpdatedTime(evaluation.getUpdatedTime());
        
        // 设置用户名（实际应用中应该从用户服务获取）
        response.setUserName("用户" + evaluation.getUserId());
        
        // 转换状态
        response.setStatus(convertStatus(evaluation.getStatus()));
        
        return response;
    }
    
    private String convertStatus(EvaluationStatus status) {
        switch (status) {
            case NORMAL: return "正常";
            case HIDDEN: return "隐藏";
            case DELETED: return "已删除";
            case PENDING: return "待审核";
            default: return status.name();
        }
    }
}
