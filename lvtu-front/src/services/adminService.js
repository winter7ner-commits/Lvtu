import request from '../utils/request'

const API_BASE_URL = '/api/admin/evaluations'

export async function getLogsByEvaluationId(evaluationId) {
  const response = await request.get(`${API_BASE_URL}/${evaluationId}/logs`)
  return response.data
}

export async function handleEvaluation(actionData) {
  const response = await request.post(`${API_BASE_URL}/actions`, actionData)
  return response.data
}
