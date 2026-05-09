
package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalDocument;
import com.bitzh.lvtu.mapper.LegalDocumentMapper;
import com.bitzh.lvtu.service.LegalDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 法律文件业务逻辑实现
 */
@Service
public class LegalDocumentServiceImpl implements LegalDocumentService {

    private final LegalDocumentMapper documentMapper;

    public LegalDocumentServiceImpl(LegalDocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    @Override
    public List&lt;LegalDocument&gt; getAllDocuments() {
        return documentMapper.selectAll();
    }

    @Override
    public List&lt;LegalDocument&gt; getDocumentsByCategoryId(Long categoryId) {
        return documentMapper.selectByCategoryIdAndStatus(categoryId, 1);
    }

    @Override
    public LegalDocument getDocumentById(Long id) {
        return documentMapper.selectById(id);
    }

    @Override
    @Transactional
    public LegalDocument createDocument(LegalDocument document) {
        documentMapper.insert(document);
        return document;
    }

    @Override
    @Transactional
    public LegalDocument updateDocument(Long id, LegalDocument document) {
        document.setId(id);
        documentMapper.update(document);
        return documentMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        documentMapper.deleteById(id);
    }
}
