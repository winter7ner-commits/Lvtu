import request from './request'

export const login = (data) => {
  return request.post('/api/auth/login', data).then((res) => res.data)
}

// export const adminLogin = (data) => {
//   return request.post('/api/auth/admin/login', data).then((res) => res.data)
// }

export const register = (data) => {
  return request.post('/api/auth/register', data).then((res) => res.data)
}
// export const adminRegister = (data) => {
//   return request.post('/api/auth/admin/register', data).then((res) => res.data)
// }
export const logout = () => {
  return request.post('/api/auth/logout').then((res) => res.data)
}

export const forgotPassword = (data) => {
  return request.post('/api/auth/forgot-password', data).then((res) => res.data)
}

export const changePassword = (data) => {
  return request.post('/api/auth/change-password', data).then((res) => res.data)
}

export const me = () => {
  return request.get('/api/auth/me').then((res) => res.data)
}
