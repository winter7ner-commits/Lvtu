
package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.ResponseDTO;
import com.bitzh.lvtu.entity.LegalDocument;
import com.bitzh.lvtu.service.LegalDocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法律文件控制器
 */
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class LegalDocumentController {

    private final LegalDocumentService documentService;

    public LegalDocumentController(LegalDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseDTO&lt;List&lt;LegalDocument&gt;&gt; getAllDocuments() {
        return ResponseDTO.success(documentService.getAllDocuments());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseDTO&lt;List&lt;LegalDocument&gt;&gt; getDocumentsByCategory(@PathVariable Long categoryId) {
        return ResponseDTO.success(documentService.getDocumentsByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseDTO&lt;LegalDocument&gt; getDocumentById(@PathVariable Long id) {
        LegalDocument document = documentService.getDocumentById(id);
        if (document == null) {
            return ResponseDTO.error("文件不存在");
        }
        return ResponseDTO.success(document);
    }

    @PostMapping
    public ResponseDTO&lt;LegalDocument&gt; createDocument(@RequestBody LegalDocument document) {
        return ResponseDTO.success(documentService.createDocument(document));
    }

    @PutMapping("/{id}")
    public ResponseDTO&lt;LegalDocument&gt; updateDocument(@PathVariable Long id, @RequestBody LegalDocument document) {
        return ResponseDTO.success(documentService.updateDocument(id, document));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO&lt;Void&gt; deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseDTO.success();
    }
}
