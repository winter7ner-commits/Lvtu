package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticleExplanation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LegalArticleExplanationMapper {
    LegalArticleExplanation findByArticleId(@Param("articleId") Long articleId);
}
