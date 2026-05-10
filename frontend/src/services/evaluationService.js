import axios from 'axios';

const API_BASE_URL = '/api/evaluations';

// 创建评价
export async function createEvaluation(evaluationData) {
  const response = await axios.post(API_BASE_URL, evaluationData);
  return response.data; // 返回评价ID
}

// 根据律师ID（或任何targetId）获取评价列表
// 后端接口是 /api/evaluations/target/{targetId}
export async function getEvaluationsByLawyer(lawyerId) {
  const response = await axios.get(`${API_BASE_URL}/target/${lawyerId}`);
  return response.data;
}

// 获取单个评价详情（可选）
export async function getEvaluationById(id) {
  const response = await axios.get(`${API_BASE_URL}/${id}`);
  return response.data;
}

// 更新评价（可选）
export async function updateEvaluation(updateData) {
  const response = await axios.put(API_BASE_URL, updateData);
  return response.data;
}

// 删除评价（可选）
export async function deleteEvaluation(id) {
  const response = await axios.delete(`${API_BASE_URL}/${id}`);
  return response.data;
}