package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.LegalArticleExplanationFeedback;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/legal-article-feedback")
public class AdminLegalArticleFeedbackController {

    private final LegalArticleService articleService;
    private final AdminPermissionService adminPermissionService;

    public AdminLegalArticleFeedbackController(LegalArticleService articleService,
                                               AdminPermissionService adminPermissionService) {
        this.articleService = articleService;
        this.adminPermissionService = adminPermissionService;
    }

    @GetMapping
    public ApiResponse<List<LegalArticleExplanationFeedback>> listFeedback(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        return ApiResponse.success(articleService.listExplanationFeedback());
    }

    @PutMapping("/{id}/handle")
    public ApiResponse<Void> markHandled(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @PathVariable Long id) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        articleService.markExplanationFeedbackHandled(id);
        return ApiResponse.success("已标记处理", null);
    }
}
