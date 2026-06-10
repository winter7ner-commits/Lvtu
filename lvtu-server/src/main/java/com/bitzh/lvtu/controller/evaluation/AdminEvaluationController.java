package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.AdminActionRequest;
import com.bitzh.lvtu.dto.evaluation.response.AdminLogResponse;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.evaluation.AdminEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/evaluations")
public class AdminEvaluationController {

    private final AdminEvaluationService adminService;
    private final AdminPermissionService adminPermissionService;

    public AdminEvaluationController(AdminEvaluationService adminService,
                                     AdminPermissionService adminPermissionService) {
        this.adminService = adminService;
        this.adminPermissionService = adminPermissionService;
    }

    @PostMapping("/actions")
    public ResponseEntity<Void> handleEvaluation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody AdminActionRequest request) {
        User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        adminService.handleEvaluation(request, admin.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{evaluationId}/logs")
    public ResponseEntity<List<AdminLogResponse>> getEvaluationLogs(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                                    @PathVariable Long evaluationId) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        List<AdminLogResponse> logs = adminService.getLogsByEvaluationId(evaluationId);
        return ResponseEntity.ok(logs);
    }
}
