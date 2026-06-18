package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateReportRequest;
import com.bitzh.lvtu.dto.evaluation.response.ReportResponse;
import com.bitzh.lvtu.service.evaluation.EvaluationReportService;
import com.bitzh.lvtu.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class EvaluationReportController {

    private final EvaluationReportService reportService;

    public EvaluationReportController(EvaluationReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateReportRequest request) {
        Long userId = requireUserId(authorization);
        ReportResponse report = reportService.createReport(request, userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/evaluations/{evaluationId}")
    public ResponseEntity<List<ReportResponse>> getReportsByEvaluationId(@PathVariable Long evaluationId) {
        List<ReportResponse> reports = reportService.getReportsByEvaluationId(evaluationId);
        return ResponseEntity.ok(reports);
    }

    private Long requireUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("未登录");
        }
        Long userId = JwtUtil.getUserId(authorization.substring(7));
        if (userId == null) {
            throw new IllegalArgumentException("无效 token");
        }
        return userId;
    }
}
