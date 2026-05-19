package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LegalArticleMapper {
    List<LegalArticle> findAll();
    LegalArticle findById(@Param("id") Long id);
    List<LegalArticle> findByDocumentId(@Param("documentId") Long documentId);
    List<LegalArticle> searchByKeyword(@Param("keyword") String keyword);
    int insert(LegalArticle article);
    int update(LegalArticle article);
    int deleteById(@Param("id") Long id);
}
