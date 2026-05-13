package com.bitzh.lvtu.service.impl.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.AdminActionRequest;
import com.bitzh.lvtu.dto.evaluation.response.AdminLogResponse;
import com.bitzh.lvtu.entity.evaluation.AdminEvaluationLog;
import com.bitzh.lvtu.entity.evaluation.Evaluation;
import com.bitzh.lvtu.enums.evaluation.ActionType;
import com.bitzh.lvtu.enums.evaluation.EvaluationStatus;
import com.bitzh.lvtu.exception.evaluation.AdminEvaluationException;
import com.bitzh.lvtu.mapper.evaluation.AdminEvaluationLogMapper;
import com.bitzh.lvtu.mapper.evaluation.EvaluationMapper;
import com.bitzh.lvtu.service.evaluation.AdminEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminEvaluationServiceImpl implements AdminEvaluationService {

    private final AdminEvaluationLogMapper logMapper;
    private final EvaluationMapper evaluationMapper;

    public AdminEvaluationServiceImpl(AdminEvaluationLogMapper logMapper,
                                     EvaluationMapper evaluationMapper) {
        this.logMapper = logMapper;
        this.evaluationMapper = evaluationMapper;
    }

    @Override
    public void handleEvaluation(AdminActionRequest request, Long adminId) {
        Evaluation evaluation = evaluationMapper.findById(request.getEvaluationId())
            .orElseThrow(() -> new AdminEvaluationException(""评价不存在""));
        
        ActionType action = ActionType.fromValue(request.getActionType());
        
        // 根据操作类型处理
        switch (action) {
            case HIDE:
                evaluation.setStatus(EvaluationStatus.HIDDEN);
                break;
            case DELETE:
                evaluation.setStatus(EvaluationStatus.DELETED);
                break;
            case RESTORE:
                evaluation.setStatus(EvaluationStatus.NORMAL);
                break;
            case FEATURE:
                evaluation.setIsFeatured(true);
                break;
            case UNFEATURE:
                evaluation.setIsFeatured(false);
                break;
            case REJECT_REPORT:
                // 这里可能需要额外的逻辑处理举报
                break;
            default:
                throw new IllegalArgumentException(""不支持的操作类型"");
        }
        
        evaluationMapper.update(evaluation);
        
        // 记录操作日志
        AdminEvaluationLog log = new AdminEvaluationLog();
        log.setEvaluationId(request.getEvaluationId());
        log.setAdminId(adminId);
        log.setActionType(action);
        log.setReason(request.getReason());
        log.setCreatedTime(LocalDateTime.now());
        
        logMapper.insert(log);
    }

    @Override
    public List<AdminLogResponse> getLogsByEvaluationId(Long evaluationId) {
        return logMapper.findByEvaluationIdOrderByCreatedTimeDesc(evaluationId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private AdminLogResponse convertToResponse(AdminEvaluationLog log) {
        AdminLogResponse dto = new AdminLogResponse();
        dto.setLogId(log.getLogId());
        dto.setEvaluationId(log.getEvaluationId());
        dto.setAdminId(log.getAdminId());
        dto.setReason(log.getReason());
        dto.setCreatedTime(log.getCreatedTime());
        
        // 设置管理员名称（实际应用中应该从用户服务获取）
        dto.setAdminName(""管理员"" + log.getAdminId());
        
        // 转换操作类型
        dto.setActionType(convertActionType(log.getActionType()));
        
        return dto;
    }
    
    private String convertActionType(ActionType actionType) {
        switch (actionType) {
            case HIDE: return ""隐藏评价"";
            case DELETE: return ""删除评价"";
            case RESTORE: return ""恢复评价"";
            case FEATURE: return ""设为精选"";
            case UNFEATURE: return ""取消精选"";
            case REJECT_REPORT: return ""驳回举报"";
            default: return actionType.name();
        }
    }
}
