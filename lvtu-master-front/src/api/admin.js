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

export const getArticleFeedback = () => {
  return request.get('/api/admin/legal-article-feedback').then((res) => res.data)
}

export const handleArticleFeedback = (id) => {
  return request.put(`/api/admin/legal-article-feedback/${id}/handle`).then((res) => res.data)
}

export const getRevisionLimitConfig = () => {
  return request.get('/api/admin/settings/revision-limit').then((res) => res.data)
}

export const updateRevisionLimitConfig = (maxRevisionRequestCount) => {
  return request.put('/api/admin/settings/revision-limit', { maxRevisionRequestCount }).then((res) => res.data)
}

export const getInterventions = () => {
  return request.get('/api/admin/interventions').then((res) => res.data)
}

export const getInterventionLogs = (orderId) => {
  return request.get(`/api/admin/interventions/${orderId}/logs`).then((res) => res.data)
}

export const handleIntervention = (orderId, data) => {
  return request.post(`/api/admin/interventions/${orderId}/actions`, data).then((res) => res.data)
}
