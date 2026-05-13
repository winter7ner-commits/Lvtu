// src/api/order.js

import request from '@/utils/request'

/**
 * 创建订单
 * 对应后端：
 * POST /api/orders
 */
export const createOrder = (data) => {
  return request({
    method: 'POST',
    url: '/orders',
    data
  })
}

/**
 * 获取订单详情
 * 对应后端：
 * GET /api/orders/{id}
 */
export const getOrderDetail = (orderId) => {
  return request({
    method: 'GET',
    url: `/orders/${orderId}`
  })
}

/**
 * 获取用户订单列表
 * 对应后端：
 * GET /api/orders/user/{userId}
 */
export const getUserOrders = (userId) => {
  return request({
    method: 'GET',
    url: `/orders/user/${userId}`
  })
}

/**
 * 修改订单状态
 * 对应后端：
 * PUT /api/orders/{id}/status?status=xxx
 *
 * 示例：
 * updateOrderStatus(1, '已支付')
 * updateOrderStatus(1, '已取消')
 */
export const updateOrderStatus = (orderId, status) => {
  return request({
    method: 'PUT',
    url: `/orders/${orderId}/status`,
    params: {
      status
    }
  })
}