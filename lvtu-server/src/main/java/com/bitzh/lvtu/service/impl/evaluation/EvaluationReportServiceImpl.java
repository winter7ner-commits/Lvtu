package com.bitzh.lvtu.service.impl.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateReportRequest;
import com.bitzh.lvtu.dto.evaluation.response.ReportResponse;
import com.bitzh.lvtu.entity.evaluation.Evaluation;
import com.bitzh.lvtu.entity.evaluation.EvaluationReport;
import com.bitzh.lvtu.enums.evaluation.ReportReason;
import com.bitzh.lvtu.enums.evaluation.ReportStatus;
import com.bitzh.lvtu.exception.evaluation.ReportException;
import com.bitzh.lvtu.mapper.evaluation.EvaluationMapper;
import com.bitzh.lvtu.mapper.evaluation.EvaluationReportMapper;
import com.bitzh.lvtu.service.evaluation.EvaluationReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationReportServiceImpl implements EvaluationReportService {

    private final EvaluationReportMapper reportMapper;
    private final EvaluationMapper evaluationMapper;

    public EvaluationReportServiceImpl(EvaluationReportMapper reportMapper,
                                      EvaluationMapper evaluationMapper) {
        this.reportMapper = reportMapper;
        this.evaluationMapper = evaluationMapper;
    }

    @Override
    public ReportResponse createReport(CreateReportRequest request, Long userId) {
        // 检查评价是否存在
        Evaluation evaluation = evaluationMapper.findById(request.getEvaluationId())
            .orElseThrow(() -> new ReportException("评价不存在"));
        
        // 检查是否已举报
        if (reportMapper.findByEvaluationIdAndReporterId(request.getEvaluationId(), userId).size() > 0) {
            throw new ReportException("已举报过该评价");
        }
        
        EvaluationReport report = new EvaluationReport();
        report.setEvaluationId(request.getEvaluationId());
        report.setReporterId(userId);
        report.setReason(ReportReason.fromValue(request.getReason()));
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedTime(LocalDateTime.now());
        
        reportMapper.insert(report);
        
        // 更新评价的举报计数
        evaluationMapper.incrementReportCount(request.getEvaluationId());
        
        return convertToResponse(report);
    }

    @Override
    public List<ReportResponse> getReportsByEvaluationId(Long evaluationId) {
        return reportMapper.findByEvaluationId(evaluationId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private ReportResponse convertToResponse(EvaluationReport report) {
        ReportResponse dto = new ReportResponse();
        dto.setReportId(report.getReportId());
        dto.setEvaluationId(report.getEvaluationId());
        dto.setReporterId(report.getReporterId());
        dto.setDescription(report.getDescription());
        dto.setCreatedTime(report.getCreatedTime());
        
        // 转换枚举为前端可读
        dto.setReason(convertReason(report.getReason()));
        dto.setStatus(convertStatus(report.getStatus()));
        
        return dto;
    }
    
    private String convertReason(ReportReason reason) {
        switch (reason) {
            case ABUSE: return "辱骂";
            case FALSE: return "虚假";
            case AD: return "广告";
            case PRIVACY: return "隐私";
            default: return reason.name();
        }
    }
    
    private String convertStatus(ReportStatus status) {
        switch (status) {
            case PENDING: return "待处理";
            case APPROVED: return "已通过";
            case REJECTED: return "已驳回";
            default: return status.name();
        }
    }
}
