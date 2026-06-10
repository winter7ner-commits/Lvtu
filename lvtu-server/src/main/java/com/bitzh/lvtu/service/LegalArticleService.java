package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.request.LegalArticleCommentRequest;
import com.bitzh.lvtu.dto.request.LegalArticleExplanationFeedbackRequest;
import com.bitzh.lvtu.dto.request.LegalArticleFavoriteRequest;
import com.bitzh.lvtu.dto.response.LegalArticleDetailResponse;
import com.bitzh.lvtu.dto.response.LegalArticleInteractionResponse;
import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.entity.LegalArticleComment;
import com.bitzh.lvtu.entity.LegalArticleExplanationFeedback;
import java.util.List;

public interface LegalArticleService {
    List<LegalArticle> getAllArticles();
    List<LegalArticle> getArticlesByDocumentId(Long documentId);
    LegalArticle getArticleById(Long id);
    LegalArticle createArticle(LegalArticle article);
    LegalArticle updateArticle(Long id, LegalArticle article);
    void deleteArticle(Long id);
    LegalArticleDetailResponse getArticleDetail(Long id, Long userId);
    List<LegalArticleComment> getComments(Long id);
    LegalArticleComment createComment(Long id, LegalArticleCommentRequest request);
    LegalArticleInteractionResponse toggleFavorite(Long id, LegalArticleFavoriteRequest request);
    LegalArticleExplanationFeedback submitExplanationFeedback(Long id, LegalArticleExplanationFeedbackRequest request);
    List<LegalArticleExplanationFeedback> listExplanationFeedback();
    void markExplanationFeedbackHandled(Long id);
}
