
package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.ResponseDTO;
import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法律条文控制器
 */
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class LegalArticleController {

    private final LegalArticleService articleService;

    public LegalArticleController(LegalArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseDTO&lt;List&lt;LegalArticle&gt;&gt; getAllArticles() {
        return ResponseDTO.success(articleService.getAllArticles());
    }

    @GetMapping("/document/{documentId}")
    public ResponseDTO&lt;List&lt;LegalArticle&gt;&gt; getArticlesByDocument(@PathVariable Long documentId) {
        return ResponseDTO.success(articleService.getArticlesByDocumentId(documentId));
    }

    @GetMapping("/{id}")
    public ResponseDTO&lt;LegalArticle&gt; getArticleById(@PathVariable Long id) {
        LegalArticle article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseDTO.error("条文不存在");
        }
        return ResponseDTO.success(article);
    }

    @PostMapping
    public ResponseDTO&lt;LegalArticle&gt; createArticle(@RequestBody LegalArticle article) {
        return ResponseDTO.success(articleService.createArticle(article));
    }

    @PutMapping("/{id}")
    public ResponseDTO&lt;LegalArticle&gt; updateArticle(@PathVariable Long id, @RequestBody LegalArticle article) {
        return ResponseDTO.success(articleService.updateArticle(id, article));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO&lt;Void&gt; deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseDTO.success();
    }
}
