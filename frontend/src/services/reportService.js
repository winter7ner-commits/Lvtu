import axios from 'axios';

const API_BASE_URL = '/api/reports';

// 创建举报
export async function createReport(reportData) {
  const response = await axios.post(API_BASE_URL, reportData);
  return response.data; // 返回举报ID
}

// 根据评价ID获取所有举报
export async function getReportsByEvaluationId(evaluationId) {
  const response = await axios.get(`${API_BASE_URL}/evaluation/${evaluationId}`);
  return response.data;
}

// 根据举报ID获取详情（可选）
export async function getReportById(id) {
  const response = await axios.get(`${API_BASE_URL}/${id}`);
  return response.data;
}