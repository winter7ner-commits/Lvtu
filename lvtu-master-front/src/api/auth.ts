import request from './request'

export const adminLogin = (data) => {
  return request.post('/api/auth/admin/login', data).then((res) => res.data)
}

export const adminRegister = (data) => {
  return request.post('/api/auth/admin/register', data).then((res) => res.data)
}