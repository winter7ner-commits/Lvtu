
package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalDocument;
import java.util.List;

/**
 * 法律文件业务逻辑接口
 */
public interface LegalDocumentService {
    List&lt;LegalDocument&gt; getAllDocuments();
    List&lt;LegalDocument&gt; getDocumentsByCategoryId(Long categoryId);
    LegalDocument getDocumentById(Long id);
    LegalDocument createDocument(LegalDocument document);
    LegalDocument updateDocument(Long id, LegalDocument document);
    void deleteDocument(Long id);
}
