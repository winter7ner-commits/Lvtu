package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.Order;
import com.bitzh.lvtu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器 (Order Controller)
 * 提供订单相关的 API 接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * 请求方式：
     * POST /api/orders
     *
     * @param order 订单实体
     * @return 创建后的订单信息
     */
    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ApiResponse.success(createdOrder);
    }

    /**
     * 获取单个订单详情
     *
     * 请求方式：
     * GET /api/orders/{id}
     *
     * @param id 订单 ID
     * @return 订单详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ApiResponse.success(order);
    }

    /**
     * 获取指定用户的全部订单
     *
     * 请求方式：
     * GET /api/orders/user/{userId}
     *
     * @param userId 用户 ID
     * @return 订单列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> getOrdersByUserId(
            @PathVariable("userId") Long userId) {

        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 修改订单状态
     *
     * 请求方式：
     * PUT /api/orders/{id}/status?status=xxx
     *
     * 示例：
     * /api/orders/1/status?status=已支付
     *
     * @param id 订单 ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        boolean result = orderService.updateOrderStatus(id, status);
        return ApiResponse.success(result);
    }
}