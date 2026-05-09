
package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.mapper.LegalArticleMapper;
import com.bitzh.lvtu.service.LegalArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 法律条文业务逻辑实现
 */
@Service
public class LegalArticleServiceImpl implements LegalArticleService {

    private final LegalArticleMapper articleMapper;

    public LegalArticleServiceImpl(LegalArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public List&lt;LegalArticle&gt; getAllArticles() {
        return articleMapper.selectAll();
    }

    @Override
    public List&lt;LegalArticle&gt; getArticlesByDocumentId(Long documentId) {
        return articleMapper.selectByDocumentIdAndStatus(documentId, 1);
    }

    @Override
    public LegalArticle getArticleById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    @Transactional
    public LegalArticle createArticle(LegalArticle article) {
        articleMapper.insert(article);
        return article;
    }

    @Override
    @Transactional
    public LegalArticle updateArticle(Long id, LegalArticle article) {
        article.setId(id);
        articleMapper.update(article);
        return articleMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }
}
