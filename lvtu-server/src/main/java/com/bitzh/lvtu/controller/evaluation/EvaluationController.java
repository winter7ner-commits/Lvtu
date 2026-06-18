package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.request.UpdateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.response.EvaluationResponse;
import com.bitzh.lvtu.service.evaluation.EvaluationService;
import com.bitzh.lvtu.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateEvaluationRequest request) {
        Long userId = requireUserId(authorization);
        EvaluationResponse evaluation = evaluationService.createEvaluation(request, userId);
        return ResponseEntity.ok(evaluation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        Long userId = requireUserId(authorization);
        EvaluationResponse evaluation = evaluationService.updateEvaluation(id, request, userId);
        return ResponseEntity.ok(evaluation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getEvaluation(@PathVariable Long id) {
        EvaluationResponse evaluation = evaluationService.getEvaluationById(id);
        return ResponseEntity.ok(evaluation);
    }

    @GetMapping("/lawyers/{lawyerId}")
    public ResponseEntity<PageInfo<EvaluationResponse>> getEvaluationsByLawyer(
            @PathVariable Long lawyerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<EvaluationResponse> evaluations = evaluationService.getEvaluationsByLawyerId(lawyerId, page, size);
        return ResponseEntity.ok(evaluations);
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
