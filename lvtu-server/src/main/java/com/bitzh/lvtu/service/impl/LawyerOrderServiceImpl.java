package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.response.AvailableOrderResponse;
import com.bitzh.lvtu.dto.response.MyOrderResponse;
import com.bitzh.lvtu.dto.response.OrderDetailResponse;
import com.bitzh.lvtu.entity.LawyerProfile;
import com.bitzh.lvtu.entity.OrderServiceDetail;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.LawyerProfileMapper;
import com.bitzh.lvtu.mapper.OrderServiceDetailMapper;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.service.LawyerOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LawyerOrderServiceImpl implements LawyerOrderService {

    private static final List<String> MY_ORDER_STATUSES = Arrays.asList("处理中", "待评价", "已完成");

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private OrderServiceDetailMapper orderServiceDetailMapper;

    @Resource
    private LawyerProfileMapper lawyerProfileMapper;

    @Override
    public List<AvailableOrderResponse> listAvailableOrders(Integer serviceTypeId) {
        return serviceOrderMapper.selectAvailableOrders(serviceTypeId)
                .stream()
                .map(this::toAvailableOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId) {
        ServiceOrder order = getRequiredOrder(orderId);
        OrderServiceDetail detail = orderServiceDetailMapper.selectByOrderId(orderId);

        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setLawyerId(order.getLawyerId());
        response.setServiceTypeId(order.getServiceTypeId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedTime(order.getCreatedTime());
        response.setUpdatedTime(order.getUpdatedTime());
        response.setFormData(detail == null ? null : detail.getFormData());
        return response;
    }

    @Override
    @Transactional
    public void acceptOrder(Long orderId, Long lawyerId) {
        validateLawyer(lawyerId);
        ServiceOrder order = getRequiredOrder(orderId);
        if (!"待接单".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许接单");
        }
        if (order.getLawyerId() != null) {
            throw new BusinessException("该订单已被接单");
        }

        int updated = serviceOrderMapper.acceptOrder(orderId, lawyerId);
        if (updated == 0) {
            throw new BusinessException("该订单已被接单");
        }
    }

    @Override
    public List<MyOrderResponse> listMyOrders(Long lawyerId, String status) {
        validateLawyer(lawyerId);
        if (status != null && !status.isBlank() && !MY_ORDER_STATUSES.contains(status)) {
            throw new BusinessException("订单状态筛选无效");
        }

        return serviceOrderMapper.selectMyOrders(lawyerId, status)
                .stream()
                .map(this::toMyOrderResponse)
                .collect(Collectors.toList());
    }

    private ServiceOrder getRequiredOrder(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectByOrderId(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private void validateLawyer(Long lawyerId) {
        if (lawyerId == null) {
            throw new BusinessException("律师ID不能为空");
        }
        LawyerProfile lawyer = lawyerProfileMapper.selectByLawyerId(lawyerId);
        if (lawyer == null) {
            throw new BusinessException("律师不存在");
        }
        if (lawyer.getStatus() == null || lawyer.getStatus() != 1) {
            throw new BusinessException("当前律师状态不可接单");
        }
    }

    private AvailableOrderResponse toAvailableOrderResponse(ServiceOrder order) {
        AvailableOrderResponse response = new AvailableOrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setServiceTypeId(order.getServiceTypeId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedTime(order.getCreatedTime());
        return response;
    }

    private MyOrderResponse toMyOrderResponse(ServiceOrder order) {
        MyOrderResponse response = new MyOrderResponse();
        response.setOrderId(order.getOrderId());
        response.setServiceTypeId(order.getServiceTypeId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedTime(order.getCreatedTime());
        response.setUpdatedTime(order.getUpdatedTime());
        return response;
    }
}
