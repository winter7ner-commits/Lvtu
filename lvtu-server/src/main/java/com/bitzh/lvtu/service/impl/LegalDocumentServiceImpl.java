package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalDocument;
import com.bitzh.lvtu.mapper.LegalDocumentMapper;
import com.bitzh.lvtu.service.LegalDocumentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalDocumentServiceImpl implements LegalDocumentService {

    private final LegalDocumentMapper documentMapper;

    public LegalDocumentServiceImpl(LegalDocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    @Override
    public List<LegalDocument> getAllDocuments() {
        return documentMapper.findAll();
    }

    @Override
    public List<LegalDocument> getDocumentsByCategoryId(Long categoryId) {
        return documentMapper.findByCategoryId(categoryId);
    }

    @Override
    public LegalDocument getDocumentById(Long id) {
        return documentMapper.findById(id);
    }

    @Override
    public LegalDocument createDocument(LegalDocument document) {
        if (document.getStatus() == null) {
            document.setStatus(1);
        }
        if (document.getSortOrder() == null) {
            document.setSortOrder(0);
        }
        documentMapper.insert(document);
        return document;
    }

    @Override
    public LegalDocument updateDocument(Long id, LegalDocument document) {
        document.setId(id);
        documentMapper.update(document);
        return document;
    }

    @Override
    public void deleteDocument(Long id) {
        documentMapper.deleteById(id);
    }
}