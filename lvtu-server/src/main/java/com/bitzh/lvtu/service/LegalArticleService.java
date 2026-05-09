
package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalArticle;
import java.util.List;

/**
 * 法律条文业务逻辑接口
 */
public interface LegalArticleService {
    List&lt;LegalArticle&gt; getAllArticles();
    List&lt;LegalArticle&gt; getArticlesByDocumentId(Long documentId);
    LegalArticle getArticleById(Long id);
    LegalArticle createArticle(LegalArticle article);
    LegalArticle updateArticle(Long id, LegalArticle article);
    void deleteArticle(Long id);
}
