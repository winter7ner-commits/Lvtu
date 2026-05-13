import axios from 'axios';

const API_BASE_URL = '/api/admin/evaluations';

// 获取某个评价的管理操作日志
export async function getLogsByEvaluationId(evaluationId, adminId) {
  const response = await axios.get(`${API_BASE_URL}/logs/${evaluationId}`, {
    headers: { 'X-Admin-Id': adminId }
  });
  return response.data;
}

// 执行管理员操作（删除评价/处理举报等）
export async function handleEvaluation(actionData, adminId) {
  // actionData 格式: { targetId, actionType, remark }
  const response = await axios.post(`${API_BASE_URL}/action`, actionData, {
    headers: { 'X-Admin-Id': adminId }
  });
  return response.data;
}