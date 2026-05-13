import request from './request'

export const createPayment = (data) => {
  return request.post('/api/payments', data).then((res) => res.data)
}

export const getPaymentById = (id) => {
  return request.get(`/api/payments/${id}`).then((res) => res.data)
}

export const getPaymentByOrderId = (orderId) => {
  return request.get(`/api/payments/order/${orderId}`).then((res) => res.data)
}

export const updatePaymentStatus = (id, status) => {
  return request.put(`/api/payments/${id}/status`, null, {
    params: { status }
  }).then((res) => res.data)
}
