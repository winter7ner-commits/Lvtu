package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LegalDocumentMapper {
    List<LegalDocument> findAll();
    List<LegalDocument> findByCategoryId(@Param("categoryId") Long categoryId);
    LegalDocument findById(@Param("id") Long id);
    int insert(LegalDocument document);
    int update(LegalDocument document);
    int deleteById(@Param("id") Long id);
}