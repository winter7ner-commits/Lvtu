package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.response.AvailableOrderResponse;
import com.bitzh.lvtu.dto.response.MyOrderResponse;
import com.bitzh.lvtu.dto.response.OrderDetailResponse;

import java.util.List;

public interface LawyerOrderService {

    List<AvailableOrderResponse> listAvailableOrders(Integer serviceTypeId);

    OrderDetailResponse getOrderDetail(Long orderId);

    void acceptOrder(Long orderId, Long lawyerId);

    List<MyOrderResponse> listMyOrders(Long lawyerId, String status);
}
