package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LawyerDTO;

import java.util.List;

/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */

public interface LawyerService {

    List<LawyerDTO> list(String specialty);

    List<LawyerDTO> getTopRatedLawyers(Integer limit);

    List<LawyerDTO> pendingList();

    void approve(Long lawyerId);

    void reject(Long lawyerId, String remark);
}