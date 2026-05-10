package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateReportRequest;
import com.bitzh.lvtu.dto.evaluation.response.ReportResponse;
import com.bitzh.lvtu.service.evaluation.EvaluationReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(""/api/reports"")
public class EvaluationReportController {

    private final EvaluationReportService reportService;

    public EvaluationReportController(EvaluationReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest request) {
        // 模拟用户ID（实际应用中应从安全上下文获取）
        Long userId = 1001L;
        ReportResponse report = reportService.createReport(request, userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping(""/evaluations/{evaluationId}"")
    public ResponseEntity<List<ReportResponse>> getReportsByEvaluationId(@PathVariable Long evaluationId) {
        List<ReportResponse> reports = reportService.getReportsByEvaluationId(evaluationId);
        return ResponseEntity.ok(reports);
    }
}
