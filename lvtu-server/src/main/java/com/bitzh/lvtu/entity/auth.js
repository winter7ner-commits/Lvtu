import request from './request'

export const login = (data) => {
  return request.post('/api/auth/login', data).then((res) => res.data)
}

export const register = (data) => {
  return request.post('/api/auth/register', data).then((res) => res.data)
}

export const logout = () => {
  return request.post('/api/auth/logout').then((res) => res.data)
}

export const forgotPassword = (data) => {
  return request.post('/api/auth/forgot-password', data).then((res) => res.data)
}
