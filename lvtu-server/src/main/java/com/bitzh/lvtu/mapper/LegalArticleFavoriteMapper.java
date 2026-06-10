package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticleFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LegalArticleFavoriteMapper {
    LegalArticleFavorite findByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);
    int countByArticleId(@Param("articleId") Long articleId);
    int insert(@Param("articleId") Long articleId, @Param("userId") Long userId);
    int delete(@Param("articleId") Long articleId, @Param("userId") Long userId);
}
