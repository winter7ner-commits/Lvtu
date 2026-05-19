package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.ApplicationDTO;

import java.util.List;

public interface ApplicationService {

    void submit(ApplicationDTO dto);

    ApplicationDTO getMyApplication(Long userId);

    List<ApplicationDTO> listAll();

    void approve(Long id);

    void reject(Long id, String reason);
}