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
        adminService.handleEvaluation(request, request.getAdminId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{evaluationId}/logs")
    public ResponseEntity<List<AdminLogResponse>> getEvaluationLogs(@PathVariable Long evaluationId) {
        List<AdminLogResponse> logs = adminService.getLogsByEvaluationId(evaluationId);
        return ResponseEntity.ok(logs);
    }
}
