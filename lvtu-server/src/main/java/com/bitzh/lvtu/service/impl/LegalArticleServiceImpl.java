package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.request.LegalArticleCommentRequest;
import com.bitzh.lvtu.dto.request.LegalArticleExplanationFeedbackRequest;
import com.bitzh.lvtu.dto.request.LegalArticleFavoriteRequest;
import com.bitzh.lvtu.dto.response.LegalArticleDetailResponse;
import com.bitzh.lvtu.dto.response.LegalArticleInteractionResponse;
import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.entity.LegalArticleComment;
import com.bitzh.lvtu.entity.LegalArticleExplanationFeedback;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.LegalArticleMapper;
import com.bitzh.lvtu.mapper.LegalArticleCommentMapper;
import com.bitzh.lvtu.mapper.LegalArticleExplanationFeedbackMapper;
import com.bitzh.lvtu.mapper.LegalArticleExplanationMapper;
import com.bitzh.lvtu.mapper.LegalArticleFavoriteMapper;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LegalArticleServiceImpl implements LegalArticleService {

    private final LegalArticleMapper articleMapper;
    private final LegalArticleExplanationMapper explanationMapper;
    private final LegalArticleCommentMapper commentMapper;
    private final LegalArticleFavoriteMapper favoriteMapper;
    private final LegalArticleExplanationFeedbackMapper feedbackMapper;

    public LegalArticleServiceImpl(LegalArticleMapper articleMapper,
                                   LegalArticleExplanationMapper explanationMapper,
                                   LegalArticleCommentMapper commentMapper,
                                   LegalArticleFavoriteMapper favoriteMapper,
                                   LegalArticleExplanationFeedbackMapper feedbackMapper) {
        this.articleMapper = articleMapper;
        this.explanationMapper = explanationMapper;
        this.commentMapper = commentMapper;
        this.favoriteMapper = favoriteMapper;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public List<LegalArticle> getAllArticles() {
        return articleMapper.findAll();
    }

    @Override
    public List<LegalArticle> getArticlesByDocumentId(Long documentId) {
        return articleMapper.findByDocumentId(documentId);
    }

    @Override
    public LegalArticle getArticleById(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    public LegalArticle createArticle(LegalArticle article) {
        if (article.getSortOrder() == null) {
            article.setSortOrder(0);
        }
        articleMapper.insert(article);
        return article;
    }

    @Override
    public LegalArticle updateArticle(Long id, LegalArticle article) {
        article.setId(id);
        articleMapper.update(article);
        return article;
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public LegalArticleDetailResponse getArticleDetail(Long id, Long userId) {
        LegalArticle article = getRequiredArticle(id);
        LegalArticleDetailResponse response = new LegalArticleDetailResponse();
        response.setArticle(article);
        response.setExplanation(explanationMapper.findByArticleId(id));
        response.setComments(commentMapper.findByArticleId(id));
        response.setCommentCount(commentMapper.countByArticleId(id));
        response.setFavoriteCount(favoriteMapper.countByArticleId(id));
        response.setFavorited(userId != null && favoriteMapper.findByArticleAndUser(id, userId) != null);
        return response;
    }

    @Override
    public List<LegalArticleComment> getComments(Long id) {
        getRequiredArticle(id);
        return commentMapper.findByArticleId(id);
    }

    @Override
    @Transactional
    public LegalArticleComment createComment(Long id, LegalArticleCommentRequest request) {
        getRequiredArticle(id);
        if (request == null || request.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException("评论内容不能为空");
        }

        LegalArticleComment comment = new LegalArticleComment();
        comment.setArticleId(id);
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent().trim());
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    @Transactional
    public LegalArticleInteractionResponse toggleFavorite(Long id, LegalArticleFavoriteRequest request) {
        getRequiredArticle(id);
        if (request == null || request.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        boolean existed = favoriteMapper.findByArticleAndUser(id, request.getUserId()) != null;
        if (existed) {
            favoriteMapper.delete(id, request.getUserId());
        } else {
            favoriteMapper.insert(id, request.getUserId());
        }

        LegalArticleInteractionResponse response = new LegalArticleInteractionResponse();
        response.setFavorited(!existed);
        response.setFavoriteCount(favoriteMapper.countByArticleId(id));
        response.setCommentCount(commentMapper.countByArticleId(id));
        return response;
    }

    @Override
    @Transactional
    public LegalArticleExplanationFeedback submitExplanationFeedback(Long id, LegalArticleExplanationFeedbackRequest request) {
        getRequiredArticle(id);
        if (request == null || request.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (request.getHelpful() == null) {
            throw new BusinessException("反馈类型不能为空");
        }

        LegalArticleExplanationFeedback feedback = new LegalArticleExplanationFeedback();
        feedback.setArticleId(id);
        feedback.setExplanationId(request.getExplanationId());
        feedback.setUserId(request.getUserId());
        feedback.setHelpful(request.getHelpful());
        feedback.setReason(trimToNull(request.getReason()));
        feedback.setContent(trimToNull(request.getContent()));
        feedbackMapper.insert(feedback);
        return feedback;
    }

    @Override
    public List<LegalArticleExplanationFeedback> listExplanationFeedback() {
        return feedbackMapper.findAll();
    }

    @Override
    @Transactional
    public void markExplanationFeedbackHandled(Long id) {
        if (id == null || feedbackMapper.markHandled(id) == 0) {
            throw new BusinessException("反馈不存在");
        }
    }

    private LegalArticle getRequiredArticle(Long id) {
        LegalArticle article = articleMapper.findById(id);
        if (article == null) {
            throw new BusinessException("条文不存在");
        }
        return article;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
