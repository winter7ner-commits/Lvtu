package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.LegalArticleCommentRequest;
import com.bitzh.lvtu.dto.request.LegalArticleExplanationFeedbackRequest;
import com.bitzh.lvtu.dto.request.LegalArticleFavoriteRequest;
import com.bitzh.lvtu.dto.response.LegalArticleDetailResponse;
import com.bitzh.lvtu.dto.response.LegalArticleInteractionResponse;
import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.entity.LegalArticleComment;
import com.bitzh.lvtu.entity.LegalArticleExplanationFeedback;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.LegalArticleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class LegalArticleController {

    private final LegalArticleService articleService;
    private final AdminPermissionService adminPermissionService;

    public LegalArticleController(LegalArticleService articleService,
                                  AdminPermissionService adminPermissionService) {
        this.articleService = articleService;
        this.adminPermissionService = adminPermissionService;
    }

    @GetMapping
    public ApiResponse<List<LegalArticle>> getAllArticles() {
        try {
            List<LegalArticle> articles = articleService.getAllArticles();
            return ApiResponse.success(articles);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取条文列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<LegalArticle> getArticleById(@PathVariable Long id) {
        try {
            LegalArticle article = articleService.getArticleById(id);
            if (article == null) {
                return ApiResponse.fail(404, "条文不存在");
            }
            return ApiResponse.success(article);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取条文失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<LegalArticleDetailResponse> getArticleDetail(@PathVariable Long id,
                                                                    @RequestParam(required = false) Long userId) {
        return ApiResponse.success(articleService.getArticleDetail(id, userId));
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<LegalArticleComment>> getComments(@PathVariable Long id) {
        return ApiResponse.success(articleService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public ApiResponse<LegalArticleComment> createComment(@PathVariable Long id,
                                                          @Valid @RequestBody LegalArticleCommentRequest request) {
        return ApiResponse.success("评论发布成功", articleService.createComment(id, request));
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<LegalArticleInteractionResponse> toggleFavorite(@PathVariable Long id,
                                                                       @Valid @RequestBody LegalArticleFavoriteRequest request) {
        return ApiResponse.success(articleService.toggleFavorite(id, request));
    }

    @PostMapping("/{id}/explanation-feedback")
    public ApiResponse<LegalArticleExplanationFeedback> submitExplanationFeedback(
            @PathVariable Long id,
            @Valid @RequestBody LegalArticleExplanationFeedbackRequest request) {
        return ApiResponse.success("反馈提交成功", articleService.submitExplanationFeedback(id, request));
    }

    @GetMapping("/document/{documentId}")
    public ApiResponse<List<LegalArticle>> getArticlesByDocument(@PathVariable Long documentId) {
        try {
            List<LegalArticle> articles = articleService.getArticlesByDocumentId(documentId);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取条文列表失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<LegalArticle> createArticle(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                   @RequestBody LegalArticle article) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            LegalArticle created = articleService.createArticle(article);
            return ApiResponse.success(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "创建条文失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<LegalArticle> updateArticle(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                   @PathVariable Long id,
                                                   @RequestBody LegalArticle article) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            LegalArticle updated = articleService.updateArticle(id, article);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "更新条文失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @PathVariable Long id) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            articleService.deleteArticle(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "删除条文失败: " + e.getMessage());
        }
    }
}
