package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.AdminActionRequest;
import com.bitzh.lvtu.dto.evaluation.response.AdminLogResponse;
import com.bitzh.lvtu.service.evaluation.AdminEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/evaluations")
public class AdminEvaluationController {

    private final AdminEvaluationService adminService;

    public AdminEvaluationController(AdminEvaluationService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/actions")
    public ResponseEntity<Void> handleEvaluation(
            @RequestBody AdminActionRequest request) {
        // 模拟管理员ID（实际应用中应从安全上下文获取）
        Long adminId = 1L;
        adminService.handleEvaluation(request, adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{evaluationId}/logs")
    public ResponseEntity<List<AdminLogResponse>> getEvaluationLogs(@PathVariable Long evaluationId) {
        List<AdminLogResponse> logs = adminService.getLogsByEvaluationId(evaluationId);
        return ResponseEntity.ok(logs);
    }
}
