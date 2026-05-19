import request from '../utils/request'

const API_BASE_URL = '/api/reports'

export async function createReport(reportData) {
  const response = await request.post(API_BASE_URL, reportData)
  return response.data
}

export async function getReportsByEvaluationId(evaluationId) {
  const response = await request.get(`${API_BASE_URL}/evaluations/${evaluationId}`)
  return response.data
}

export async function getReportById(id) {
  const response = await request.get(`${API_BASE_URL}/${id}`)
  return response.data
}
