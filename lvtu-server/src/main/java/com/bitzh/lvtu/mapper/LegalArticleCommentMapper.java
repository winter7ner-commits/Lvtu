package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticleComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LegalArticleCommentMapper {
    List<LegalArticleComment> findByArticleId(@Param("articleId") Long articleId);
    int countByArticleId(@Param("articleId") Long articleId);
    int insert(LegalArticleComment comment);
}
