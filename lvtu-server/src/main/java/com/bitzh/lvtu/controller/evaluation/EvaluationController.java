package com.bitzh.lvtu.controller.evaluation;

import com.bitzh.lvtu.dto.evaluation.request.CreateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.request.UpdateEvaluationRequest;
import com.bitzh.lvtu.dto.evaluation.response.EvaluationResponse;
import com.bitzh.lvtu.service.evaluation.EvaluationService;
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
            @Valid @RequestBody CreateEvaluationRequest request) {
        // 模拟用户ID（实际应用中应从安全上下文获取）
        Long userId = 1001L;
        EvaluationResponse evaluation = evaluationService.createEvaluation(request, userId);
        return ResponseEntity.ok(evaluation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        // 模拟用户ID（实际应用中应从安全上下文获取）
        Long userId = 1001L;
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
}
