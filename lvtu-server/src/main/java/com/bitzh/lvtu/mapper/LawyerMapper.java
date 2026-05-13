package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.dto.LawyerDTO;
import com.bitzh.lvtu.entity.Lawyer;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface LawyerMapper {

    int insertLawyer(Lawyer lawyer);

    int updateLawyer(Lawyer lawyer);

    // 查询全部律师（已审核）
    List<LawyerDTO> selectLawyerList();

    // 获取评分最高的律师
    List<LawyerDTO> selectByTopRating(@Param("limit") Integer limit);

    // 按领域筛选
    List<LawyerDTO> selectBySpecialty(@Param("specialty") String specialty);

    // 查询待审核律师
    List<LawyerDTO> selectPendingList();

    // 审核通过
    int approve(@Param("lawyerId") Long lawyerId);

    // 审核拒绝
    int reject(@Param("lawyerId") Long lawyerId,
               @Param("remark") String remark);
}