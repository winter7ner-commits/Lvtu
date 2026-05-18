package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(Long id);
    List<Order> getOrdersByUserId(Long userId);
    boolean updateOrderStatus(Long id, String status);
}

