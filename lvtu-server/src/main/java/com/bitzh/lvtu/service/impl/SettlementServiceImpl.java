package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.response.SettlementResponse;
import com.bitzh.lvtu.entity.LawyerSettlement;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.ServiceResult;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.LawyerSettlementMapper;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.mapper.ServiceResultMapper;
import com.bitzh.lvtu.service.SettlementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private ServiceResultMapper serviceResultMapper;

    @Resource
    private LawyerSettlementMapper lawyerSettlementMapper;

    @Override
    @Transactional
    public void confirmOrder(Long orderId, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        ServiceOrder order = serviceOrderMapper.selectByOrderId(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("只有下单用户可以确认订单完成");
        }
        if (!"待评价".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许确认完成");
        }
        if (order.getLawyerId() == null) {
            throw new BusinessException("订单尚未分配律师");
        }

        ServiceResult result = serviceResultMapper.selectByOrderId(orderId);
        if (result == null) {
            throw new BusinessException("服务结果不存在");
        }
        if (!order.getLawyerId().equals(result.getLawyerId())) {
            throw new BusinessException("服务结果与订单律师信息不一致");
        }

        LawyerSettlement existingSettlement = lawyerSettlementMapper.selectByOrderId(orderId);
        if (existingSettlement != null) {
            throw new BusinessException("该订单的结算记录已存在");
        }

        int updated = serviceOrderMapper.updateStatusWithCurrent(orderId, "待评价", "已完成");
        if (updated == 0) {
            throw new BusinessException("订单确认失败");
        }

        serviceResultMapper.updateStatusByOrderId(orderId, 1);

        LawyerSettlement settlement = new LawyerSettlement();
        settlement.setOrderId(orderId);
        settlement.setLawyerId(order.getLawyerId());
        settlement.setAmount(order.getTotalAmount());
        settlement.setStatus(0);
        lawyerSettlementMapper.insert(settlement);
    }

    @Override
    public List<SettlementResponse> listPendingSettlements() {
        return lawyerSettlementMapper.selectPendingSettlements().stream().map(this::toSettlementResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void paySettlement(Long settlementId) {
        LawyerSettlement settlement = lawyerSettlementMapper.selectById(settlementId);
        if (settlement == null) {
            throw new BusinessException("结算记录不存在");
        }
        if (settlement.getStatus() != null && settlement.getStatus() == 1) {
            throw new BusinessException("该结算记录已结算");
        }

        int updated = lawyerSettlementMapper.markAsPaid(settlementId);
        if (updated == 0) {
            throw new BusinessException("结算失败");
        }
    }

    private SettlementResponse toSettlementResponse(LawyerSettlement settlement) {
        SettlementResponse response = new SettlementResponse();
        response.setId(settlement.getId());
        response.setOrderId(settlement.getOrderId());
        response.setLawyerId(settlement.getLawyerId());
        response.setAmount(settlement.getAmount());
        response.setStatus(settlement.getStatus());
        response.setSettledAt(settlement.getSettledAt());
        response.setCreatedAt(settlement.getCreatedAt());
        return response;
    }
}
