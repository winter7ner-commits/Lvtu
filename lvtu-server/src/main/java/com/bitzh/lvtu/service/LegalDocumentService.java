package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalDocument;
import java.util.List;

public interface LegalDocumentService {
    List<LegalDocument> getAllDocuments();
    List<LegalDocument> getDocumentsByCategoryId(Long categoryId);
    LegalDocument getDocumentById(Long id);
    List<LegalDocument> searchByName(String name);
    LegalDocument createDocument(LegalDocument document);
    LegalDocument updateDocument(Long id, LegalDocument document);
    void deleteDocument(Long id);
}