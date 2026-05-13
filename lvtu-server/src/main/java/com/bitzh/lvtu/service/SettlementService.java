package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.response.SettlementResponse;

import java.util.List;

public interface SettlementService {

    void confirmOrder(Long orderId, Long userId);

    List<SettlementResponse> listPendingSettlements();

    void paySettlement(Long settlementId);
}
