import request from './request'

export const getUsers = () => {
  return request.get('/api/admin/users').then((res) => res.data)
}

export const getAuthApplications = () => {
  return request.get('/api/admin/auth-applications').then((res) => res.data)
}

export const getOrders = () => {
  return request.get('/api/admin/orders').then((res) => res.data)
}

export const handleEvaluation = (data) => {
  return request.post('/api/admin/evaluations/actions', data).then((res) => res.data)
}

export const getEvaluationLogs = (evaluationId) => {
  return request.get(`/api/admin/evaluations/${evaluationId}/logs`).then((res) => res.data)
}
