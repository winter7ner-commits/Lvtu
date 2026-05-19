package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalArticle;
import java.util.List;

public interface LegalArticleService {
    List<LegalArticle> getAllArticles();
    List<LegalArticle> getArticlesByDocumentId(Long documentId);
    LegalArticle getArticleById(Long id);
    List<LegalArticle> searchByKeyword(String keyword);
    LegalArticle createArticle(LegalArticle article);
    LegalArticle updateArticle(Long id, LegalArticle article);
    void deleteArticle(Long id);
}
