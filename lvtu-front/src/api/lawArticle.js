import request from '@/utils/request'

export function getCategories() {
  return request({
    url: '/api/categories',
    method: 'get'
  })
}

export function getDocumentsByCategory(categoryId) {
  return request({
    url: `/api/documents/category/${categoryId}`,
    method: 'get'
  })
}

export function getAllDocuments() {
  return request({
    url: '/api/documents',
    method: 'get'
  })
}

export function getArticlesByDocument(documentId) {
  return request({
    url: `/api/articles/document/${documentId}`,
    method: 'get'
  })
}

export function getAllArticles() {
  return request({
    url: '/api/articles',
    method: 'get'
  })
}

export function getArticleDetail(articleId, userId) {
  return request({
    url: `/api/articles/${articleId}/detail`,
    method: 'get',
    params: userId ? { userId } : {}
  })
}

export function toggleArticleFavorite(articleId, userId) {
  return request({
    url: `/api/articles/${articleId}/favorite`,
    method: 'post',
    data: { userId }
  })
}

export function createArticleComment(articleId, data) {
  return request({
    url: `/api/articles/${articleId}/comments`,
    method: 'post',
    data
  })
}

export function submitExplanationFeedback(articleId, data) {
  return request({
    url: `/api/articles/${articleId}/explanation-feedback`,
    method: 'post',
    data
  })
}
