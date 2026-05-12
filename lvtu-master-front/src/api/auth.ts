import request from './request'

export const adminLogin = (data: { username: string; password: string }) => {
  return request.post('/api/auth/admin/login', data).then((res) => res.data)
}

export const adminRegister = (data: { username: string; password: string; email: string; phone: string; userType: number; }) => {
  return request.post('/api/auth/admin/register', data).then((res) => res.data)
}