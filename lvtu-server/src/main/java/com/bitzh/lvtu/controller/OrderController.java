package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.CreateOrderRequest;
import com.bitzh.lvtu.entity.Order;
import com.bitzh.lvtu.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器 (Order Controller)
 * 提供订单相关的API接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 创建订单
     * @param order 订单实体
     * @return 返回创建后的订单信息
     */
    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = toOrder(request);
            Order createdOrder = orderService.createOrder(order);
            return ApiResponse.success(createdOrder);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "创建订单失败");
        }
    }

    private Order toOrder(CreateOrderRequest request) throws JsonProcessingException {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setLawyerId(request.getLawyerId());
        order.setServiceTypeId(request.getServiceTypeId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(request.getStatus());

        Object formData = request.getFormData();
        if (formData instanceof String text) {
            order.setFormData(text);
        } else if (formData != null) {
            order.setFormData(objectMapper.writeValueAsString(formData));
        }
        return order;
    }

    /**
     * 获取单个订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ApiResponse.success(order);
    }

    /**
     * 获取特定用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> getOrdersByUserId(@PathVariable("userId") Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        boolean result = orderService.updateOrderStatus(id, status);
        return ApiResponse.success(result);
    }
}
