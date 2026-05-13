package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LawyerProfile;
import org.apache.ibatis.annotations.Param;

public interface LawyerProfileMapper {

    LawyerProfile selectByLawyerId(@Param("lawyerId") Long lawyerId);
}
