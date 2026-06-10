package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticleExplanationFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LegalArticleExplanationFeedbackMapper {
    int insert(LegalArticleExplanationFeedback feedback);
    List<LegalArticleExplanationFeedback> findAll();
    int markHandled(@Param("id") Long id);
}
