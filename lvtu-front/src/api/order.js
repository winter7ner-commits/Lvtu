import request from './request'

export const createOrder = (data) => {
  return request.post('/api/orders', data).then((res) => res.data)
}

export const getOrderById = (id) => {
  return request.get(`/api/orders/${id}`).then((res) => res.data)
}

export const getOrdersByUserId = (userId) => {
  return request.get(`/api/orders/user/${userId}`).then((res) => res.data)
}

export const updateOrderStatus = (id, status) => {
  return request.put(`/api/orders/${id}/status`, null, {
    params: { status }
  }).then((res) => res.data)
}
