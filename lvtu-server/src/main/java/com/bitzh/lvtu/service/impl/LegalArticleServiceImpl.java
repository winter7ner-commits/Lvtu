package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.mapper.LegalArticleMapper;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalArticleServiceImpl implements LegalArticleService {

    private final LegalArticleMapper articleMapper;

    public LegalArticleServiceImpl(LegalArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public List<LegalArticle> getAllArticles() {
        return articleMapper.findAll();
    }

    @Override
    public LegalArticle getArticleById(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    public LegalArticle createArticle(LegalArticle article) {
        if (article.getStatus() == null) {
            article.setStatus(1);
        }
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
}