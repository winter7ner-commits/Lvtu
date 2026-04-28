package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.LawyerDTO;
import com.bitzh.lvtu.mapper.LawyerMapper;
import com.bitzh.lvtu.service.LawyerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */

@Service
public class LawyerServiceImpl implements LawyerService {

    @Resource
    private LawyerMapper mapper;

    @Override
    public List<LawyerDTO> list(String specialty) {
        if (specialty == null || specialty.isEmpty()) {
            return mapper.selectLawyerList();
        }
        return mapper.selectBySpecialty(specialty);
    }

    @Override
    public List<LawyerDTO> getTopRatedLawyers(Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 3 : limit;
        return mapper.selectByTopRating(safeLimit);
    }

    @Override
    public List<LawyerDTO> pendingList() {
        return mapper.selectPendingList();
    }

    @Override
    public void approve(Long lawyerId) {
        mapper.approve(lawyerId);
    }

    @Override
    public void reject(Long lawyerId, String remark) {
        mapper.reject(lawyerId, remark);
    }
}