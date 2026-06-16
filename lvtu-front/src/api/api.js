// 导入Axios用于HTTP请求
import axios from 'axios'

// 后端API基础URL
const baseURL = '/api'

// 法律法规分类API
const categoryAPI = {
  // 获取所有分类
  getAll: () => axios.get(`${baseURL}/categories`),
  // 根据父ID获取分类
  getByParent: (parentId) => axios.get(`${baseURL}/categories/parent/${parentId}`),
  // 根据ID获取分类详情
  getById: (id) => axios.get(`${baseURL}/categories/${id}`),
  // 创建分类
  create: (data) => axios.post(`${baseURL}/categories`, data),
  // 更新分类
  update: (id, data) => axios.put(`${baseURL}/categories/${id}`, data),
  // 删除分类
  delete: (id) => axios.delete(`${baseURL}/categories/${id}`)
}

// 法律法规文件API
const documentAPI = {
  // 获取所有文件
  getAll: () => axios.get(`${baseURL}/documents`),
  // 根据分类获取文件
  getByCategory: (categoryId) => axios.get(`${baseURL}/documents/category/${categoryId}`),
  // 根据ID获取文件详情
  getById: (id) => axios.get(`${baseURL}/documents/${id}`),
  // 创建文件
  create: (data) => axios.post(`${baseURL}/documents`, data),
  // 更新文件
  update: (id, data) => axios.put(`${baseURL}/documents/${id}`, data),
  // 删除文件
  delete: (id) => axios.delete(`${baseURL}/documents/${id}`)
}

// 法律条文API
const articleAPI = {
  // 获取所有条文
  getAll: () => axios.get(`${baseURL}/articles`),
  // 根据文件获取条文
  getByDocument: (documentId) => axios.get(`${baseURL}/articles/document/${documentId}`),
  // 根据ID获取条文详情
  getById: (id) => axios.get(`${baseURL}/articles/${id}`),
  // 创建条文
  create: (data) => axios.post(`${baseURL}/articles`, data),
  // 更新条文
  update: (id, data) => axios.put(`${baseURL}/articles/${id}`, data),
  // 删除条文
  delete: (id) => axios.delete(`${baseURL}/articles/${id}`)
}

// 用户API
const userAPI = {
  // 用户登录
  login: (data) => axios.post(`${baseURL}/users/login`, data),
  // 用户注册
  register: (data) => axios.post(`${baseURL}/users/register`, data),
  // 重置密码
  resetPassword: (data) => axios.post(`${baseURL}/users/reset-password`, data)
}

// 导出API模块供其他组件使用
export { categoryAPI, documentAPI, articleAPI, userAPI }
