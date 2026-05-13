package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.OrderDTO;
import com.bitzh.lvtu.entity.Order;
import com.bitzh.lvtu.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Order createOrder(Order order) {
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return new Order();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return new ArrayList<>();
    }

    @Override
    public boolean updateOrderStatus(Long id, String status) {
        return true;
    }

    @Override
    public List<OrderDTO> getOrderList(String status, List<Integer> serviceTypeIds, String keyword, int page, int pageSize) {
        return List.of();
    }

    @Override
    public boolean cancelOrder(Long id) {
        return false;
    }

    @Override
    public boolean payOrder(Long id) {
        return false;
    }
}