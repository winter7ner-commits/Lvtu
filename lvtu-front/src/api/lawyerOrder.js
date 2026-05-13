import request from '@/utils/request'

export function getAvailableOrders(params) {
  return request({
    url: '/api/lawyer/orders/available',
    method: 'get',
    params
  })
}

export function getLawyerOrderDetail(orderId) {
  return request({
    url: `/api/lawyer/orders/${orderId}`,
    method: 'get'
  })
}

export function acceptLawyerOrder(orderId, lawyerId) {
  return request({
    url: `/api/lawyer/orders/${orderId}/accept`,
    method: 'post',
    data: { lawyerId }
  })
}

export function getMyLawyerOrders(params) {
  return request({
    url: '/api/lawyer/orders/my',
    method: 'get',
    params
  })
}

export function submitServiceResult(orderId, data) {
  return request({
    url: `/api/lawyer/orders/${orderId}/result`,
    method: 'post',
    data
  })
}

export function getServiceResult(orderId, lawyerId) {
  return request({
    url: `/api/lawyer/orders/${orderId}/result`,
    method: 'get',
    params: { lawyerId }
  })
}
