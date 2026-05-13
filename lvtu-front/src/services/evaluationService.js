import request from '../utils/request'

const API_BASE_URL = '/api/evaluations'

export async function createEvaluation(evaluationData) {
  const response = await request.post(API_BASE_URL, evaluationData)
  return response.data
}

export async function getEvaluationsByLawyer(lawyerId, page = 1, size = 10) {
  const response = await request.get(`${API_BASE_URL}/lawyers/${lawyerId}`, {
    params: { page, size }
  })
  return response.data
}

export async function getEvaluationById(id) {
  const response = await request.get(`${API_BASE_URL}/${id}`)
  return response.data
}

export async function updateEvaluation(id, updateData) {
  const response = await request.put(`${API_BASE_URL}/${id}`, updateData)
  return response.data
}

export async function deleteEvaluation(id) {
  const response = await request.delete(`${API_BASE_URL}/${id}`)
  return response.data
}
