package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.LegalDocument;
import com.bitzh.lvtu.service.LegalDocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class LegalDocumentController {

    private final LegalDocumentService documentService;

    public LegalDocumentController(LegalDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ApiResponse<List<LegalDocument>> getAllDocuments() {
        try {
            List<LegalDocument> documents = documentService.getAllDocuments();
            return ApiResponse.success(documents);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取文件列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<LegalDocument>> getDocumentsByCategory(@PathVariable Long categoryId) {
        try {
            List<LegalDocument> documents = documentService.getDocumentsByCategoryId(categoryId);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取文件列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<LegalDocument> getDocumentById(@PathVariable Long id) {
        try {
            LegalDocument document = documentService.getDocumentById(id);
            if (document == null) {
                return ApiResponse.fail(404, "文件不存在");
            }
            return ApiResponse.success(document);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<LegalDocument>> searchDocumentsByName(@RequestParam String name) {
        try {
            List<LegalDocument> documents = documentService.searchByName(name);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "搜索文件失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<LegalDocument> createDocument(@RequestBody LegalDocument document) {
        try {
            LegalDocument created = documentService.createDocument(document);
            return ApiResponse.success(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "创建文件失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<LegalDocument> updateDocument(@PathVariable Long id, @RequestBody LegalDocument document) {
        try {
            LegalDocument updated = documentService.updateDocument(id, document);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "更新文件失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "删除文件失败: " + e.getMessage());
        }
    }
}
