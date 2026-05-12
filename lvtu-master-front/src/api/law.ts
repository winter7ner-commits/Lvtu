import request from './request'

export const getCategories = () => {
  return request.get('/api/categories').then((res) => res.data)
}

export const createCategory = (data) => {
  return request.post('/api/categories', data).then((res) => res.data)
}

export const updateCategory = (id, data) => {
  return request.put(`/api/categories/${id}`, data).then((res) => res.data)
}

export const deleteCategory = (id) => {
  return request.delete(`/api/categories/${id}`).then((res) => res.data)
}

export const getDocuments = () => {
  return request.get('/api/documents').then((res) => res.data)
}

export const createDocument = (data) => {
  return request.post('/api/documents', data).then((res) => res.data)
}

export const updateDocument = (id, data) => {
  return request.put(`/api/documents/${id}`, data).then((res) => res.data)
}

export const deleteDocument = (id) => {
  return request.delete(`/api/documents/${id}`).then((res) => res.data)
}

export const getArticles = () => {
  return request.get('/api/articles').then((res) => res.data)
}

export const createArticle = (data) => {
  return request.post('/api/articles', data).then((res) => res.data)
}

export const updateArticle = (id, data) => {
  return request.put(`/api/articles/${id}`, data).then((res) => res.data)
}

export const deleteArticle = (id) => {
  return request.delete(`/api/articles/${id}`).then((res) => res.data)
}
