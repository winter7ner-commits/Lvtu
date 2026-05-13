package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LawyerSettlement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LawyerSettlementMapper {

    int insert(LawyerSettlement settlement);

    LawyerSettlement selectByOrderId(@Param("orderId") Long orderId);

    LawyerSettlement selectById(@Param("id") Long id);

    List<LawyerSettlement> selectPendingSettlements();

    int markAsPaid(@Param("id") Long id);
}
