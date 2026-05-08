package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.entity.LegalDocument;
import com.bitzh.lvtu.repository.LegalArticleRepository;
import com.bitzh.lvtu.repository.LegalCategoryRepository;
import com.bitzh.lvtu.repository.LegalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LegalController {

    @Autowired
    private LegalCategoryRepository categoryRepository;

    @Autowired
    private LegalDocumentRepository documentRepository;

    @Autowired
    private LegalArticleRepository articleRepository;

    @GetMapping("/categories")
    public List<LegalCategory> getAllCategories() {
        return categoryRepository.findByStatus(1);
    }

    @GetMapping("/documents")
    public List<LegalDocument> getDocumentsByCategory(@RequestParam Integer categoryId) {
        return documentRepository.findByCategoryId(categoryId);
    }

    @GetMapping("/articles")
    public List<LegalArticle> getArticlesByDocument(@RequestParam Integer documentId) {
        return articleRepository.findByDocumentIdOrderBySortOrder(documentId);
    }

    @GetMapping("/article/{id}")
    public LegalArticle getArticleById(@PathVariable Integer id) {
        return articleRepository.findById(id).orElse(null);
    }

    @GetMapping("/document/{id}")
    public LegalDocument getDocumentById(@PathVariable Integer id) {
        return documentRepository.findById(id).orElse(null);
    }

    @GetMapping("/category/{id}")
    public LegalCategory getCategoryById(@PathVariable Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }
}
