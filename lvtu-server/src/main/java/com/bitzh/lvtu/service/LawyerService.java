package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LawyerDTO;

import java.util.List;

public interface LawyerService {

    List<LawyerDTO> list(String specialty);

    List<LawyerDTO> getTopRatedLawyers(Integer limit);
}