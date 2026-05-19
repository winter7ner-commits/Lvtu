package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class LegalArticleController {

    private final LegalArticleService articleService;

    public LegalArticleController(LegalArticleService articleService) {
        this.articleService = articleService;
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

    @GetMapping("/search")
    public ApiResponse<List<LegalArticle>> searchArticlesByKeyword(@RequestParam String keyword) {
        try {
            List<LegalArticle> articles = articleService.searchByKeyword(keyword);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "搜索条文失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<LegalArticle> createArticle(@RequestBody LegalArticle article) {
        try {
            LegalArticle created = articleService.createArticle(article);
            return ApiResponse.success(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "创建条文失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<LegalArticle> updateArticle(@PathVariable Long id, @RequestBody LegalArticle article) {
        try {
            LegalArticle updated = articleService.updateArticle(id, article);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "更新条文失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "删除条文失败: " + e.getMessage());
        }
    }
}
