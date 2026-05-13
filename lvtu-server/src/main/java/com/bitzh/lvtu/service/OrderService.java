package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(Long id);
    List<Order> getOrdersByUserId(Long userId);
    boolean updateOrderStatus(Long id, String status);
    
    // 获取带过滤/分页的订单列表
    List<com.bitzh.lvtu.dto.OrderDTO> getOrderList(String status, List<Integer> serviceTypeIds, String keyword, int page, int pageSize);
    // 取消订单
    boolean cancelOrder(Long id);
    // 支付订单
    boolean payOrder(Long id);
}
