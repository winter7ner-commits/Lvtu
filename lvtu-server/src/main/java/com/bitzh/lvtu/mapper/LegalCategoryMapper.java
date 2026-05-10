package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LegalCategoryMapper {
    List<LegalCategory> findAll();
    LegalCategory findById(@Param("id") Long id);
    int insert(LegalCategory category);
    int update(LegalCategory category);
    int deleteById(@Param("id") Long id);
}