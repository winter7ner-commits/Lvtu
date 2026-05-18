package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.Specialty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpecialtyMapper {

    // 基础 CRUD 操作
    int insert(Specialty specialty);

    int update(Specialty specialty);

    int deleteById(@Param("specialty_id") Integer specialty_id);

    // 按领域id搜索对应领域
    Specialty selectById(@Param("specialtyId") Integer specialtyId);

    // 搜索所有领域 / 显示所有领域
    List<Specialty> selectAll();
}
