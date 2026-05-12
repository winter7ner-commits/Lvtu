package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.ApplicationDTO;
import com.bitzh.lvtu.entity.Application;
import com.bitzh.lvtu.entity.Lawyer;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.ApplicationMapper;
import com.bitzh.lvtu.mapper.LawyerMapper;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.ApplicationService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private LawyerMapper lawyerMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public void submit(ApplicationDTO dto) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 2. 检查是否已实名认证
        if (user.getIsVerified() == null || !user.getIsVerified()) {
            throw new RuntimeException("请先完成实名认证");
        }

        // 3. 检查是否存在待审核的申请
        ApplicationDTO existing = applicationMapper.selectByUserId(dto.getUserId());
        if (existing != null && existing.getStatus() == 0) {
            throw new RuntimeException("您已有待审核的申请，请耐心等待");
        }
        // 4. 如果已认证为律师，且本次申请类型不为变更，则不允许
        if (existing != null && existing.getStatus() == 1 && dto.getApplyType() != 1) {
            throw new RuntimeException("您已是认证律师，如需修改资料请使用「资料变更」功能");
        }

        Application app = new Application();
        app.setUserId(dto.getUserId());
        app.setLicenseNo(dto.getLicenseNo());
        app.setLicenseUrl(dto.getLicenseUrl());
        app.setLawFirm(dto.getLawFirm());
        app.setPracticeStartYear(dto.getPracticeStartYear());
        app.setApplyType(dto.getApplyType() != null ? dto.getApplyType() : 0);
        app.setStatus(0);
        applicationMapper.insert(app);
        // 更新用户表的认证状态为“审核中”
        userMapper.updateAuthStatus(dto.getUserId(), 1);
    }

    @Override
    public ApplicationDTO getMyApplication(Long userId) {
        return applicationMapper.selectByUserId(userId);
    }

    @Override
    public List<ApplicationDTO> listAll() {
        return applicationMapper.selectAll();
    }

    @Override
    @Transactional
    public void approve(Long id) {
        ApplicationDTO dto = applicationMapper.selectByApplicationId(id);
        if (dto == null) throw new RuntimeException("申请记录不存在");
        if (dto.getStatus() != 0) throw new RuntimeException("该申请已被处理");

        applicationMapper.approveApplication(id);
        int years = Year.now().getValue() - dto.getPracticeStartYear();

        if (dto.getApplyType() == 0) { // 首次入驻
            Lawyer lawyer = new Lawyer();
            lawyer.setUserId(dto.getUserId());
            lawyer.setLicenseNo(dto.getLicenseNo());
            lawyer.setLawFirm(dto.getLawFirm());
            lawyer.setBio("");
            lawyer.setPracticeYears(years);
            lawyer.setRating(0.0);
            lawyer.setStatus(1);
            lawyerMapper.insertLawyer(lawyer);
        } else { // 资料变更 – 更新律师信息
            Lawyer lawyer = new Lawyer();
            lawyer.setUserId(dto.getUserId());
            lawyer.setLicenseNo(dto.getLicenseNo());
            lawyer.setLawFirm(dto.getLawFirm());
            lawyer.setPracticeYears(years);

            lawyerMapper.updateLawyer(lawyer);
        }

        // 更新用户认证状态为“已认证律师”
        userMapper.updateAuthStatus(dto.getUserId(), 2);
        // 更新用户类型为律师
        userMapper.updateUserType(dto.getUserId(), 2);
    }

    @Override
    @Transactional
    public void reject(Long id, String reason) {
        applicationMapper.rejectApplication(id, reason);
        ApplicationDTO dto = applicationMapper.selectByApplicationId(id);
        if (dto != null) {
            userMapper.updateAuthStatus(dto.getUserId(), 3);
        }
    }
}